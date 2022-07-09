package com.news.article.controller;

import com.news.api.BaseController;
import com.news.api.controller.article.ArticleControllerApi;
import com.news.article.service.ArticleService;
import com.news.common.enums.ArticleCoverType;
import com.news.common.enums.ArticleReviewStatus;
import com.news.common.enums.YesOrNo;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.NewArticleBO;
import com.news.model.user.pojo.Category;
import com.news.model.user.vo.ArticleDetailVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.*;
import java.util.*;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章入库接口
 * @create 2022-06-23-10:47
 */
@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    @Autowired
    ArticleService articleService;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${freemarker.html.article}")
    String articlePath;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public R createArticle(@Valid NewArticleBO newArticleBO) {

        //1.1 校验result合法性

        //1.2 判断文章封面图类型合法性，单图必填，纯文字设置为空
        if(newArticleBO.getArticleType() == ArticleCoverType.ONE_IMAGE.type){
            //若选择文章封面图但未上传，抛错误
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return R.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        }else{
            //不选择图片封面
            newArticleBO.setArticleCover("");
        }

        //1.3 校验分类id合法性
        //从缓存中获取所有文章分类信息
        String allCategorysJson = (String) redisTemplate.opsForValue().get(REDIS_ALL_CATEGORY);
        if(StringUtils.isBlank(allCategorysJson)){
            return R.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        List<Category> categoryList = JsonUtils.jsonToList(allCategorysJson, Category.class);
        //判断传入的分类id是否符合
        Boolean isVaild = articleService.isCategoryIdVaild(categoryList, newArticleBO.getCategoryId());
        if (isVaild == false) {
            //分类id不合法，抛错误
            return R.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
        }

        //3.发布文章
        articleService.createArticle(newArticleBO);

        return R.ok();
    }

    @Override
    public R queryMyList(String userId, String keyword,
                         Integer status,
                         Date startDate, Date endDate,
                         Integer page, Integer pageSize) {

        //1.合法性校验
        if (StringUtils.isBlank(userId)) {
            return R.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        //2.构建分页查询
        PagedGridResult gridResult = articleService.queryMyArticleList(userId, keyword,
                                                                       status,
                                                                       startDate, endDate,
                                                                       page, pageSize);
        //返回分页数据
        return R.ok(gridResult);
    }

    @Override
    public R queryAllList(Integer status,
                          Integer page,
                          Integer pageSize) {

        //构建分页查询
        PagedGridResult gridResult = articleService.queryAllArticleList(status, page, pageSize);

        return R.ok(gridResult);
    }

    @Override
    public R doReview(String articleId, Integer passOrNot) {

        //1.参数合法性校验
        if(passOrNot != YesOrNo.YES.type && passOrNot != YesOrNo.NO.type){
            return R.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        //2.进行状态修改
        Integer pendingStatus;
        if(passOrNot == YesOrNo.YES.type){
            //审核通过
            pendingStatus = ArticleReviewStatus.SUCCESS.type; //SUCCESS(3, "审核通过（已发布）")
        }else{
            //审核不通过
            pendingStatus = ArticleReviewStatus.FAILED.type; //FAILED(4, "审核未通过")
        }

        //3.更新数据库数据，更新文章的状态
        articleService.updateArticleStatus(articleId, pendingStatus);

        //4.生成文章详情页静态html
//        if(pendingStatus == ArticleReviewStatus.SUCCESS.type){
//            try {
//                createArticleHTML(articleId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        return R.ok();
    }

    @Override
    public R withdraw(String userId, String articleId) {
        //用户撤销文章操作
        articleService.withdrawArticle(userId, articleId);

        return R.ok();
    }

    @Override
    public R delete(String userId, String articleId) {
        //用户删除文章操作
        articleService.deleteArticle(userId, articleId);

        return R.ok();
    }

    // 文章生成HTML
    private void createArticleHTML(String articleId) throws Exception{

        //0.配置freemarker基本环境,声明freemarker模板所需要加载的目录的位置
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        //1.获得现有的模板ftl文件
        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        //2.获得动态数据 —— 文章详情数据
        ArticleDetailVo detailVo = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVo);

        //3.融合动态数据和ftl，生成html到windows
        File tempDic = new File(articlePath);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }

        String finalPath = articlePath + File.separator + detailVo.getId() + ".html";
        Writer out = null;
        try {
            out = new FileWriter(finalPath);
            template.process(map, out);
        } finally {
            out.close();
        }

        //4.将windows文件上传到Ngnix中
    }

    //远程调用，获得文章详情数据
    private ArticleDetailVo getArticleDetail(String articleId){

        String url = "http://win.news.com:8001/portal/article/detail?articleId=" + articleId;
        ResponseEntity<R> userBaseInfoEntity = restTemplate.getForEntity(url, R.class);

        //构建用户基本信息列表
        R body = userBaseInfoEntity.getBody();
        ArticleDetailVo detailVo = null;
        if(body.getStatus() == 200){
            String articleDetailJson = JsonUtils.objectToJson(body.getData());
            detailVo = JsonUtils.jsonToPojo(articleDetailJson, ArticleDetailVo.class);
        }else{
            GraceException.display(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        return detailVo;
    }

//    /**
//     * @param directory   远程目录的位置
//     * @param winFilePath   本地文件名称
//     * @throws IOException
//     */
//    public void insertFile(String directory,String winFilePath) throws IOException {
//        //匿名登录（无需帐号密码的FTP服务器）
//        //Ftp ftp = new Ftp("远程host", 服务端口号, "远程登录用户", "远程登录密码",Charset.forName("utf-8"));
//        Ftp ftp = new Ftp(
//                "192.168.200.130",
//                22,
//                "root",
//                "root",
//                Charset.forName("utf-8"));
//        try {
//            //启动被动模式
//            ftp.setMode(FtpMode.Passive);
//            //进入远程目录
//            ftp.cd(directory);
//            //上传本地文件
//            boolean flag = ftp.upload(directory, FileUtil.file(winFilePath));
//
//            System.err.println( flag ? "上传成功" : "上传失败！！");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //关闭连接
//            ftp.close();
//        }
//    }

}
