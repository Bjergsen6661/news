package com.news.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.news.api.BaseController;
import com.news.api.config.RabbitMQDelayConfig;
import com.news.api.controller.elasticsearch.EsArticleControllerApi;
import com.news.article.mapper.ArticleMapper;
import com.news.article.mapper.DiyArticleMapper;
import com.news.article.service.ArticleService;
import com.news.common.enums.ArticleAppointType;
import com.news.common.enums.ArticleReviewLevel;
import com.news.common.enums.ArticleReviewStatus;
import com.news.common.enums.YesOrNo;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.DateUtil;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.NewArticleBO;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.pojo.Article;
import com.news.model.user.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.news.api.BaseController.REDIS_ARTICLE_COMMENT_COUNTS;
import static com.news.api.BaseController.REDIS_ARTICLE_READ_COUNTS;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章入库业务实现
 * @create 2022-06-23-10:58
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private Sid sid;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    DiyArticleMapper diyArticleMapper;

    @Autowired
    BaseController baseController;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    EsArticleControllerApi esClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void createArticle(NewArticleBO newArticleBO) {

        //设置主键
        String articleId = sid.nextShort();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);

        //填充其它字段
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type); //初次提交 -> 审核中
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        //发布时间设定
        if (article.getIsAppoint() == YesOrNo.YES.type) {
            //如果选择预约发布时间，则需要条填充发布时间
            article.setPublishTime(newArticleBO.getPublishTime());
        } else if (article.getIsAppoint() == YesOrNo.NO.type) {
            //否则按照用户当前提交时间
            article.setPublishTime(new Date());
        }

        //保存至数据库持久化
        int result = articleMapper.insert(article);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        // 发送延迟消息到mq，计算发布时间和当前时间的时间差，则为往后延迟的时间
        if (article.getIsAppoint() == ArticleAppointType.TIMING.type) {
            //计算时间差
            Date futureDate = newArticleBO.getPublishTime();
            Date nowDate = new Date();
            int delayTimes = (int)(futureDate.getTime() - nowDate.getTime());
            System.out.println(DateUtil.timeBetween(futureDate, nowDate));

            MessagePostProcessor messagePostProcessor = message -> {
                // 设置持久
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                // 设置延迟的时间，单位毫秒
                message.getMessageProperties().setDelay(delayTimes);

                return message;
            };

            // 发送延迟消息
            rabbitTemplate.convertAndSend(
                    RabbitMQDelayConfig.EXCHANGE_DELAY,
                    "publish.delay.display",
                    articleId, //传送文章id至消费端
                    messagePostProcessor);

            System.out.println("延迟消息-定时发布：" + new Date());
        }


        //AI审核(模拟) —— 暂不实现，直接交给人工审核
//        String reviewResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getTitle() + newArticleBO.getContent());
        String reviewResult = "review"; //手动填写AI审核结果 {pass、block、review}

        if(reviewResult.equals(ArticleReviewLevel.PASS.type)){
            //修改当前文章状态，标记为审核通过...
            log.info("审核通过...");
            this.updateArticleStatus(articleId, ArticleReviewStatus.SUCCESS.type);
        }else if(reviewResult.equals(ArticleReviewLevel.BLOCK.type)){
            //修改当前文章状态，标记为审核不通过...
            log.info("审核不通过...");
            this.updateArticleStatus(articleId, ArticleReviewStatus.FAILED.type);
        }else if(reviewResult.equals(ArticleReviewLevel.REVIEW.type)){
            //修改当前文章状态，标记为需要人工审核...
            log.info("需要人工审核...");
            this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);
        }


    }

    @Override
    public boolean isCategoryIdVaild(List<Category> categoryList, Integer categoryId) {
        for(Category c : categoryList){
            if(c.getId() == categoryId){
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateAppointToPublish() {

        //把数据库中文章发布状态由1变为0
        diyArticleMapper.updateAppointToPublish();
    }

    @Override
    public PagedGridResult queryMyArticleList(String userId, String keyword,
                                              Integer status,
                                              Date startDate, Date endDate,
                                              Integer page, Integer pageSize) {

        //构建查询 —— 查询用户的所有文章
        Example articleExample = new Example(Article.class);
        articleExample.orderBy("createTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();

        //1.通过用户id查询所属文章
        criteria.andEqualTo("publishUserId", userId);

        //2.通过关键字模糊查询所属文章
        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        //3.查询规定状态的文章
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        // 审核中（前端规定：'12'） -> [机审]和[人工审核]两个状态
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        //4.通过日期限定搜索文章
        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("publishTime", startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }

        //5.只能展示未删除的文章给用户
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        //构建分页数据
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);

        //6.添加上文章阅读量与评论数
        addReadsAndComments(list);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        //构建查询 —— 查询指定articleId文章
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("id", articleId);

        //修改文章状态
        Article pending = new Article();
        pending.setArticleStatus(pendingStatus);
        int result = articleMapper.updateByExampleSelective(pending, articleExample);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        //如果审核通过，则查询Article，把数据字段信息存入es
        if(pendingStatus == ArticleReviewStatus.SUCCESS.type){
            Article article = articleMapper.selectByPrimaryKey(articleId);
            // 如果是即时发布的文章，审核通过后则可以直接存入es中
            if(article.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type) {
                ArticleEo eo = new ArticleEo();
                eo.setId(articleId);
                eo.setTitle(article.getTitle());
                eo.setCategoryId(article.getCategoryId());
                eo.setArticleType(article.getArticleType().longValue());
                eo.setArticleCover(article.getArticleCover());
                eo.setPublishUserId(article.getPublishUserId());
                eo.setPublishTime(article.getPublishTime());

                //创建es文档
                esClient.addDocument(eo);
            }
            //定时发布的文章，此处不能放入es，需要在定时的延迟队列中执行
            else{
                //计算时间差
                Date futureDate = article.getPublishTime();
                Date nowDate = new Date();
                int delayTimes = (int)(futureDate.getTime() - nowDate.getTime());
                System.out.println(DateUtil.timeBetween(futureDate, nowDate));

                MessagePostProcessor messagePostProcessor = message -> {
                    // 设置持久
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    // 设置延迟的时间，单位毫秒
                    message.getMessageProperties().setDelay(delayTimes);

                    return message;
                };

                String articleJson = JsonUtils.objectToJson(article);
                // 发送延迟消息
                rabbitTemplate.convertAndSend(
                        RabbitMQDelayConfig.EXCHANGE_DELAY,
                        "espublish.delay.display",
                        articleJson, //传送文章实体信息至消费端
                        messagePostProcessor);

                System.out.println("延迟消息-定时发布：" + new Date());
            }
        }
    }

    @Override
    public PagedGridResult queryAllArticleList(Integer status, Integer page, Integer pageSize) {

        //构建查询 —— 查询用户的所有文章
        Example articleExample = new Example(Article.class);
        articleExample.orderBy("createTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();

        //1.查询规定状态的文章
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        // 审核中（前端规定：'12'） -> [机审]和[人工审核]两个状态
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        //构建分页数据
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);

        //添加上阅读量和评论数
        addReadsAndComments(list);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public void withdrawArticle(String userId, String articleId) {
        //构建查询
        Example articleExample = makeExampleCriteria(userId, articleId);

        //将文章状态设为撤回
        Article article = new Article();
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);
        int result = articleMapper.updateByExampleSelective(article, articleExample);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }

        //删除es中的文章
        esClient.deleteDocument(articleId);
    }

    @Override
    public void deleteArticle(String userId, String articleId) {
        //构建查询
        Example articleExample = makeExampleCriteria(userId, articleId);

        //将文章设为删除状态
        Article article = new Article();
        article.setIsDelete(YesOrNo.YES.type);
        int result = articleMapper.updateByExampleSelective(article, articleExample);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }

        //删除es中的文章
        esClient.deleteDocument(articleId);
    }

    @Transactional
    @Override
    public void updateArticleToPublish(String articleId) {
        //根据文章id，修改文章发布状态为'即时发布'
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);

        articleMapper.updateByPrimaryKeySelective(article);
    }


    //构建查询用户id+文章id的example
    private Example makeExampleCriteria(String userId, String articleId) {

        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);

        return articleExample;
    }

    /**
     * 根据文章id集合，去redis中查询阅读量和评论数
     * @param list 文章id集合
     */
    private void addReadsAndComments(List<Article> list) {
        //构建文章id列表,使用mget在redis中查询阅读量和评论数
        List<String> idListReadCounts = new ArrayList<>();
        List<String> idListCommentCounts = new ArrayList<>();
        for (Article article : list) {
            idListReadCounts.add(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
            idListCommentCounts.add(REDIS_ARTICLE_COMMENT_COUNTS + ":" + article.getId());
        }
        List<String> readCountsList = redisTemplate.opsForValue().multiGet(idListReadCounts);
        List<String> commentCountsList = redisTemplate.opsForValue().multiGet(idListCommentCounts);

        for(int i = 0; i < list.size(); i++){
            Article article = list.get(i);

            //设置阅读量
            Object o1 = readCountsList.get(i);
            String redisreadCountsStr = JsonUtils.objectToJson(o1);
            if(StringUtils.isBlank(redisreadCountsStr) || o1 == null){
                redisreadCountsStr = "0";
            }
            int readCounts = Integer.parseInt(redisreadCountsStr);

            //设置评论量
            Object o2 = commentCountsList.get(i);
            String rediscommentCountsStr = JsonUtils.objectToJson(o2);
            if(StringUtils.isBlank(rediscommentCountsStr) || o2 == null){
                rediscommentCountsStr = "0";
            }
            int commentCounts = Integer.parseInt(rediscommentCountsStr);

            article.setReadCounts(readCounts);
            article.setCommentCounts(commentCounts);
        }
    }

}
