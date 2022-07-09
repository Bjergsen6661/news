package com.news.elasticsearch.service.impl;

import com.news.common.utils.JsonUtils;
import com.news.elasticsearch.constants.ArticleConstants;
import com.news.elasticsearch.service.EsArticleService;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.pojo.Article;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description article 索引库相关业务实现
 * @create 2022-07-03-12:31
 */
@Service
public class EsArticleServiceImpl implements EsArticleService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public void createIndex() {
        //1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("article");

        //2.准备请求的参数，DSL语句
        request.source(ArticleConstants.MAPPING_ARTICLE, XContentType.JSON);

        //3.发送请求
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDoc(ArticleEo articleEo) {
        //1.创建request对象 —— 文档id与文章id一致
        IndexRequest request = new IndexRequest("article");
        request.id(articleEo.getId());

        //2.准备Json数据
        String articleJson = JsonUtils.objectToJson(articleEo);
        request.source(articleJson, XContentType.JSON);

        //3.发送请求
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delDoc(String articleId) {
        //1.准备request
        DeleteRequest request = new DeleteRequest("article", articleId);

        //2.发送请求
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyPageResult search(String keyword, Integer category, Integer page, Integer pageSize) {
        //1.准备request
        SearchRequest request = new SearchRequest("article");

        //2.准备DSL
        //2.1、query
        buildBasicQuery(keyword, category, page, request);
        //2.2、排序
        request.source().sort("publishTime", SortOrder.DESC);
        //2.3、分页
        request.source().from((page - 1) * pageSize).size(pageSize);
        //2.4、高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title")
                        .preTags("<font color='red'>")
                        .postTags("</font>")
                        .requireFieldMatch(false);
        request.source().highlighter(highlightBuilder);

        //3.发送请求
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.解析响应 —— 构建自定义分页数据
        return handleResponse(page, pageSize, response);
    }

    @Override
    public List<ArticleEo> hotList(Set<String> articleIds) {
        List<ArticleEo> list = new ArrayList<>();

        for(String aId : articleIds){
            //1.准备request
            GetRequest request = new GetRequest("article", aId);

            //2.发送请求，得到响应
            GetResponse response = null;
            try {
                response = client.get(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //3.解析响应结果
            String json = response.getSourceAsString();
            ArticleEo eo = JsonUtils.jsonToPojo(json, ArticleEo.class);
            list.add(eo);
        }

        return list;
    }

    @Override
    public List<ArticleEo> getWriterArticles(String writerId) {
        //1.准备request
        SearchRequest request = new SearchRequest("article");

        //2.准备DSL
        //2.1、query
        request.source().query(QueryBuilders.matchQuery("publishUserId", writerId));
        request.source().size(1000);

        //3.发送请求
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.解析响应
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        List<ArticleEo> list = new ArrayList<>();
        for(SearchHit hit : hits){
            String json = hit.getSourceAsString();
            ArticleEo eo = JsonUtils.jsonToPojo(json, ArticleEo.class);
            list.add(eo);
        }

        return list;
    }


    /**
     * 构建布尔查询
     */
    private void buildBasicQuery(String keyword, Integer category, Integer page, SearchRequest request) {
        if(page < 1) return;

        // 1.构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 2.关键字搜索
        if (keyword == null || "".equals(keyword)) {
            //不使用关键字查询就是全匹配
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("title", keyword));
        }

        // 3.文章分类条件
        if (category != null) {
            boolQuery.filter(QueryBuilders.termQuery("categoryId", category));
        }

        // 4.放入source
        request.source().query(boolQuery);
    }

    /**
     * 处理response，封装查询数据
     */
    private MyPageResult handleResponse(Integer page, Integer pageSize, SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;

        // 4.2.遍历获取文档数组
        SearchHit[] hits = searchHits.getHits();
        List<ArticleEo> articles = new ArrayList<>();
        for(SearchHit hit : hits){
            // 4.2.1、获取文档sourse
            String json = hit.getSourceAsString();
            // 4.2.2、反序列化
            ArticleEo eo = JsonUtils.jsonToPojo(json, ArticleEo.class);
            //4.2.3、获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(!CollectionUtils.isEmpty(highlightFields)){
                //根据字段名获取高亮结果
                HighlightField highlightField = highlightFields.get("title");
                if(highlightField != null){
                    //获取高亮值
                    String title = highlightField.getFragments()[0].string();
                    //覆盖非高亮结果
                    eo.setTitle(title);
                }
            }
            articles.add(eo);
        }

        // 4.3、数据封装返回
        MyPageResult res = new MyPageResult();
        res.setTotal(total);
        res.setArticleEos(articles);

        return res;
    }

}
