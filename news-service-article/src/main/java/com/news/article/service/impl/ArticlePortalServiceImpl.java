package com.news.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.news.api.BaseController;
import com.news.api.controller.elasticsearch.EsArticleControllerApi;
import com.news.api.controller.user.UserControllerApi;
import com.news.article.feignClient.UserClient;
import com.news.article.mapper.ArticleMapper;
import com.news.article.service.ArticlePortalService;
import com.news.common.enums.ArticleReviewStatus;
import com.news.common.enums.YesOrNo;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.IPUtil;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.pojo.Article;
import com.news.model.user.vo.ArticleDetailVo;
import com.news.model.user.vo.ArticleandUserBaseInfoVo;
import com.news.model.user.vo.UserBaseInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.news.api.BaseController.*;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户首页文章业务实现
 * @create 2022-06-24-11:57
 */
@Slf4j
@Service
public class ArticlePortalServiceImpl implements ArticlePortalService {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    BaseController baseController;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserClient userClient;

    @Autowired
    EsArticleControllerApi esClient;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category,
                                                 Integer page, Integer pageSize) {

        //1.构建查询
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExampleCriteria(articleExample);

        // 构建附带查询
        if (StringUtils.isNotBlank(keyword)) {
            //拼接关键字查询
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (category != null) {
            //拼接文章分类查询
            criteria.andEqualTo("categoryId", category);
        }

        //　构建分页数据
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryIndexArticleListByEs(String keyword, Integer category,
                                                     Integer page, Integer pageSize) {

        R search = esClient.search(keyword, category, page, pageSize);
        MyPageResult res = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            res = JsonUtils.jsonToPojo(searchJson, MyPageResult.class);
        }else{
            res = new MyPageResult();
        }

        //构建分页数据
        long allRecords = res.getTotal();
        long allPages = allRecords % pageSize == 0 ? allRecords / pageSize : allRecords / pageSize + 1;
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page); //当前页码
        grid.setRows(res.getArticleEos()); //详情数据
        grid.setTotal(allPages); //总页数
        grid.setRecords(allRecords); //总记录数

        return grid;
    }

    @Override
    public List<Article> queryHotArticleList() {

        /***************************************基于数据库查询最新5篇文章******************************************/
        //根据文章阅读量，构建查询
//        Example articleExample = new Example(Article.class);
//        Example.Criteria criteria = setDefaultArticleExampleCriteria(articleExample);
//
//        //构建分页数据
//        PageHelper.startPage(1, 5);
//        List<Article> list = articleMapper.selectByExample(articleExample);


        /***************************************基于es查询阅读量最多的5篇文章******************************************/
        Set<String> set = new HashSet<>();
        for(long i = -1; i >= -5; i--){
            Set<String> temp = redisTemplate.opsForZSet().range(REDIS_ARTICLE_READ_COUNTS_ZSETS, i, i);
            for(String t : temp){
                set.add(t);
            }
        }

        System.out.println("size:" + set.size());
        for(String s : set){
            System.out.println("文章id:" + s);
        }

        //通过获取的文章id，去es中查询对应文章的信息
        R search = esClient.hotList(set);
        List<Article> list = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            List<ArticleEo> eoList = JsonUtils.jsonToList(searchJson, ArticleEo.class);
            //把ArticleEo转为Article
            list = eoList.stream().map(item -> {
                Article article = new Article();
                BeanUtils.copyProperties(item, article);
                return article;
            }).collect(Collectors.toList());

            //构建文章id列表，去redis中查询阅读量
            ArrayList<String> idLists = new ArrayList<>();
            for(String s : set){
                idLists.add(REDIS_ARTICLE_READ_COUNTS + ":" + s);
            }
            //发起redis的mget批量查询
            List<String> readCountsList = redisTemplate.opsForValue().multiGet(idLists);

            //填充文章阅读量，并且按阅读量从大到小排序
            getTopArticle(list, readCountsList);

        }else{
            list = new ArrayList<>();
        }

        return list;
    }

    @Override
    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        //构建查询 + 隐性条件查询
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExampleCriteria(articleExample);
        criteria.andEqualTo("publishUserId", writerId);

        //构建分页数据
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfWriter(String writerId) {
        /**********************************基于数据库查询作家的所有文章************************************/
//        //构建查询
//        Example articleExample = new Example(Article.class);
//        articleExample.orderBy("publishTime").desc();
//        Example.Criteria criteria = setDefaultArticleExampleCriteria(articleExample);
//        criteria.andEqualTo("publishUserId", writerId);
//
//        //构建分页数据
//        PageHelper.startPage(1, 5);
//        List<Article> list = articleMapper.selectByExample(articleExample);


        /***********************************基于es查询作家的所有文章****************************/
        R search = esClient.getWriterArticles(writerId);
        List<ArticleEo> eoList = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            eoList = JsonUtils.jsonToList(searchJson, ArticleEo.class);
        }else{
            eoList = new ArrayList<>();
        }
        //把ArticleEo转化为Article
        List<Article> list = eoList.stream().map(item ->{
            Article article = new Article();
            BeanUtils.copyProperties(item, article);
            return article;
        }).collect(Collectors.toList());

        //构建文章id的rediskey列表
        ArrayList<String> idList = new ArrayList<>();
        for(Article a : list){
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());
        }
        //发起redis的mget批量查询
        List<String> readCountsList = redisTemplate.opsForValue().multiGet(idList);

        //填充文章阅读量，并且按阅读量从大到小排序
        getTopArticle(list, readCountsList);

        List<Article> res = new ArrayList<>();
        for(int i = 0; i < 5 && i < list.size(); i++){
            if(list.get(i).getReadCounts() != 0) res.add(list.get(i));
        }
        //构建分页数据
        PageHelper.startPage(1, 5);
        return baseController.setterPagedGrid(res, 1);
    }


    @Override
    public ArticleDetailVo detail(String articleId) {
        //设置文章基本信息，用数据库查，es中未存放'内容'和'分类'字段
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.type);
        article.setIsAppoint(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        article = articleMapper.selectOne(article);

        //判空检测
        if(article == null || article.getId() == null){
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        //数据拷贝
        ArticleDetailVo detailVO = new ArticleDetailVo();
        BeanUtils.copyProperties(article, detailVO);
        //article与detailVO处字段名不一致，手动赋值
        detailVO.setCover(article.getArticleCover());

        //远程调用,通过id查询用户名，添加为作者名
        Set<String> idSet = new HashSet<>();
        idSet.add(detailVO.getPublishUserId());
        List<UserBaseInfoVo> publisherList = getPublisherListByFeign(idSet);

        if (!publisherList.isEmpty()) {
            String publishName = publisherList.get(0).getNickname();
            detailVO.setPublishUserName(publishName);
        }

        //获取redis中该文章的阅读数
        String readsKey = REDIS_ARTICLE_READ_COUNTS + ":" + articleId;
        Integer readsCounts = baseController.getCountsFromRedis(readsKey);
        detailVO.setReadCounts(readsCounts);

        return detailVO;
    }

    /**
     * 重构文章响应实体，加上用户基本数据
     * @param articleLists 初始文章分页数据
     */
    @Override
    public PagedGridResult rebuildArticleGrid(PagedGridResult articleLists) {
        //0.页面渲染需要文章信息，对应用户的'头像信息'和'用户名信息'
        List<Article> articles = (List<Article>)articleLists.getRows();

        //1.1 构建发布者id的列表,放入set中去重记录
        //1.2 构建文章id的列表
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article article : articles) {
            idSet.add(article.getPublishUserId());
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
        }
        //1.3 发起redis的mget批量查询
        List<String> readCountsList = redisTemplate.opsForValue().multiGet(idList);

        //2.将用户id远程调用，查询用户基本信息列表
        List<UserBaseInfoVo> publisherList = getPublisherListByFeign(idSet);

        //3.重组文章列表，加上用户基本信息
        List<ArticleandUserBaseInfoVo> articleandUserBaseInfoVoList = new ArrayList<>();
        for(int i = 0; i < articles.size(); i++){
            ArticleandUserBaseInfoVo infoVo = new ArticleandUserBaseInfoVo();
            Article article = articles.get(i);

            //3.1 先填充文章信息
            BeanUtils.copyProperties(article, infoVo);

            //3.2 填充用户基本信息
            UserBaseInfoVo baseInfoVo = getUserIfEqualPublisher(article.getPublishUserId(), publisherList);
            //若用户信息获取异常，就不显示用户信息
            infoVo.setPublisherVo(baseInfoVo);

            //3.3 填充文章阅读量 —— 使用 mget() 优化，一次请求批量获取
            Object o = readCountsList.get(i);
            String redisCountsStr = JsonUtils.objectToJson(o);
            if(StringUtils.isBlank(redisCountsStr) || o == null){
                redisCountsStr = "0";
            }
            int readCounts = Integer.parseInt(redisCountsStr);
            infoVo.setReadCounts(readCounts);

            articleandUserBaseInfoVoList.add(infoVo);
        }

        //4.重新设置分页行信息
        articleLists.setRows(articleandUserBaseInfoVoList);

        return articleLists;
    }

    /**
     * 重构文章响应实体，加上用户基本数据
     * @param articleLists 初始文章分页数据
     */
    @Override
    public PagedGridResult rebuildArticleGridEo(PagedGridResult articleLists) {
        //0.页面渲染需要文章信息，对应用户的'头像信息'和'用户名信息'
        List<ArticleEo> articles = (List<ArticleEo>)articleLists.getRows();

        //1.1 构建发布者id的列表,放入set中去重记录
        //1.2 构建文章id的列表
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (ArticleEo article : articles) {
            idSet.add(article.getPublishUserId());
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" +article.getId());
        }
        //1.3 发起redis的mget批量查询
        List<String> readCountsList = redisTemplate.opsForValue().multiGet(idList);

        //2.将用户id远程调用，查询用户基本信息列表
        List<UserBaseInfoVo> publisherList = getPublisherListByFeign(idSet);

        //3.重组文章列表，加上用户基本信息
        List<ArticleandUserBaseInfoVo> articleandUserBaseInfoVoList = new ArrayList<>();
        for(int i = 0; i < articles.size(); i++){
            ArticleandUserBaseInfoVo infoVo = new ArticleandUserBaseInfoVo();
            ArticleEo article = articles.get(i);

            //3.1 先填充文章信息
//            BeanUtils.copyProperties(article, infoVo);
            infoVo.setId(article.getId());
            infoVo.setTitle(article.getTitle());
            infoVo.setCategoryId(article.getCategoryId());
            infoVo.setArticleType(article.getArticleType().intValue());
            infoVo.setArticleCover(article.getArticleCover());
            infoVo.setPublishUserId(article.getPublishUserId());
            infoVo.setPublishTime(article.getPublishTime());

            //3.2 填充用户基本信息
            UserBaseInfoVo baseInfoVo = getUserIfEqualPublisher(article.getPublishUserId(), publisherList);
            //若用户信息获取异常，就不显示用户信息
            infoVo.setPublisherVo(baseInfoVo);

            //3.3 填充文章阅读量 —— 使用 mget() 优化，一次请求批量获取
            Object o = readCountsList.get(i);
            String redisCountsStr = JsonUtils.objectToJson(o);
            if(StringUtils.isBlank(redisCountsStr) || o == null){
                redisCountsStr = "0";
            }
            int readCounts = Integer.parseInt(redisCountsStr);
            infoVo.setReadCounts(readCounts);

            articleandUserBaseInfoVoList.add(infoVo);
        }

        //4.重新设置分页行信息
        articleLists.setRows(articleandUserBaseInfoVoList);

        return articleLists;
    }

    @Override
    public void increaseReads(String articleId, HttpServletRequest request) {
        // 设置永久存在key，表示该用户已经阅读过该文章了，无法累加阅读量
        String userIP = IPUtil.getRequestIp(request);
        redisTemplate.opsForValue().setIfAbsent(ARTICLE_ALREADY_READ + ":" + articleId + ":" + userIP, userIP + "_" + articleId);

        // redis 文章阅读数累加
        String readsKey = REDIS_ARTICLE_READ_COUNTS + ":" + articleId;
        redisTemplate.opsForValue().increment(readsKey, 1);

        //redis，zset记录文章阅读数，存储文章id
        String setName = REDIS_ARTICLE_READ_COUNTS_ZSETS;
        redisTemplate.opsForZSet().incrementScore(setName, articleId, 1);
    }

    //隐性条件查询
    private Example.Criteria setDefaultArticleExampleCriteria(Example articleExample) {
        articleExample.orderBy("publishTime").desc();

        /**
         * 自带查询条件：
         * isPoint为即时发布，表示文章已经直接发布，或者定时任务到点发布
         * isDelete为未删除，表示文章不能展示已经被删除的
         * status为审核通过，表示文章经过机审/人审通过
         */
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);

        return criteria;
    }


    //从publisherList中获得匹配的用户
    private UserBaseInfoVo getUserIfEqualPublisher(String publisherId, List<UserBaseInfoVo> publisherList) {
        for (UserBaseInfoVo publisher : publisherList) {
            if (publisherId.equalsIgnoreCase(publisher.getId())) {
                publisher.setFault(false);
                return publisher;
            }
        }

        //若都不匹配，标识无此用户文章信息，通过降级处理显示默认头像，不显示用户名
        UserBaseInfoVo userBaseInfoVo = new UserBaseInfoVo();
        userBaseInfoVo.setFault(true); //是经过降级后的用户信息
        return userBaseInfoVo;
    }

    /**
     * @param idSet 去重的id列表
     */
    private List<UserBaseInfoVo> getPublisherListByFeign(Set<String> idSet){
        //构建用户基本信息列表
        /******************************* 使用Feign远程调用 *************************************/
        R body = userClient.queryByIds(JsonUtils.objectToJson(idSet));
        log.info("feign远程调用：queryByIds 查询用户基本信息...");

        List<UserBaseInfoVo> userBaseInfoVoList = null;
        if(body.getStatus() == 200){
            String userlistsJson = JsonUtils.objectToJson(body.getData());
            userBaseInfoVoList = JsonUtils.jsonToList(userlistsJson, UserBaseInfoVo.class);
        }else{
            userBaseInfoVoList = new ArrayList<>();
        }

        return userBaseInfoVoList;
    }

    /**
     * 填充文章阅读量，并且按阅读量从大到小排序
     * @param list es中查询到的文章
     * @param readCountsList redis中根据文章id查询得到的阅读量
     */
    private void getTopArticle(List<Article> list, List<String> readCountsList) {
        //填充文章阅读量
        for(int i = 0; i < list.size(); i++){
            Article article = list.get(i);
            Object o = readCountsList.get(i);
            String redisCountsStr = JsonUtils.objectToJson(o);
            if(StringUtils.isBlank(redisCountsStr) || o == null){
                redisCountsStr = "0";
            }
            int readCounts = Integer.parseInt(redisCountsStr);
            article.setReadCounts(readCounts);
        }

        //展示该作者前五阅读量的文章
        Comparator<Article> listComparator = (o1, o2) -> o2.getReadCounts() - o1.getReadCounts();
        list.sort(listComparator);
    }

}
