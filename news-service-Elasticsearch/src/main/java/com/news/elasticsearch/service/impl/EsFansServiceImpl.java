package com.news.elasticsearch.service.impl;

import com.news.common.enums.Sex;
import com.news.common.utils.JsonUtils;
import com.news.elasticsearch.constants.ArticleConstants;
import com.news.elasticsearch.constants.FansConstants;
import com.news.elasticsearch.service.EsFansService;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.eo.FansEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description fans 索引库相关业务实现
 * @create 2022-07-04-13:06
 */
@Service
public class EsFansServiceImpl implements EsFansService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public void createIndex() {
        //1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("fans");

        //2.准备请求的参数，DSL语句
        request.source(FansConstants.MAPPING_FANS, XContentType.JSON);

        //3.发送请求
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDoc(FansEo fansEo) {
        //1.创建request对象 —— 文档id：作家id+粉丝id
        IndexRequest request = new IndexRequest("fans");
        String docId = fansEo.getWriterId() + "_" + fansEo.getFanId();
        request.id(docId);

        //2.准备Json数据
        String articleJson = JsonUtils.objectToJson(fansEo);
        request.source(articleJson, XContentType.JSON);

        //3.发送请求
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delDoc(String writerId, String fansId) {
        //1.准备request
        DeleteRequest request = new DeleteRequest("fans", writerId + "_" + fansId);

        //2.发送请求
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyPageResult search(String writerId, Integer page, Integer pageSize) {
        //1.准备request
        SearchRequest request = new SearchRequest("fans");

        //2.准备DSL
        //2.1、query
        buildBasicQuery(writerId, page, request);
        //2.2、分页
        request.source().from((page - 1) * pageSize).size(pageSize);

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
    public void updateDoc(FansEo fansEo) {
        //准备Source
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", fansEo.getId());
        updateMap.put("writerId", fansEo.getWriterId());
        updateMap.put("fanId", fansEo.getFanId());
        updateMap.put("face", fansEo.getFace());
        updateMap.put("fanNickname", fansEo.getFanNickname());
        updateMap.put("sex", fansEo.getSex());
        updateMap.put("province", fansEo.getProvince());

        //1.准备request
        String docId = fansEo.getWriterId() + "_" + fansEo.getFanId();
        UpdateRequest request = new UpdateRequest("fans", docId);

        //2.准备请求参数
        request.doc(updateMap);

        //3.发送请求
        try {
            client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FansCountsVo getSexCounts(String writerId) {
        //1.准备request
        SearchRequest request = new SearchRequest("fans");

        //2.准备DSL
        //2.1、设置query，查询作家所属的粉丝
        request.source().query(QueryBuilders.matchQuery("writerId", writerId));
        //2.2、设置size 0
        request.source().size(0);
        //2.3、设置agg聚合，划分性别
        request.source().aggregation(AggregationBuilders
                .terms("sex_counts")
                .field("sex")
        );

        //3.发送请求
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.解析响应
        FansCountsVo fansCountsVo = new FansCountsVo();
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> sexRatio = getAggByName(aggregations, "sex_counts");
        System.out.println("展示粉丝性别分布：" + sexRatio.toString());

        for(Map.Entry entry : sexRatio.entrySet()){
            String keyJson = JsonUtils.objectToJson(entry.getKey());
            Integer sex = JsonUtils.jsonToPojo(keyJson, Integer.class);
            String valJson = JsonUtils.objectToJson(entry.getValue());
            Integer counts = JsonUtils.jsonToPojo(valJson, Integer.class);
            if(sex == Sex.woman.type){
                fansCountsVo.setWomanCounts(counts);
            }else if(sex == Sex.man.type){
                fansCountsVo.setManCounts(counts);
            }
        }

        return fansCountsVo;
    }

    @Override
    public Map<String, Integer> getRegionCounts(String writerId) {
        //1.准备request
        SearchRequest request = new SearchRequest("fans");

        //2.准备DSL
        //2.1、设置query，查询作家所属的粉丝
        request.source().query(QueryBuilders.matchQuery("writerId", writerId));
        //2.2、设置size 0
        request.source().size(0);
        //2.3、设置agg聚合，划分地域
        request.source().aggregation(AggregationBuilders
                .terms("region_counts")
                .field("province")
        );

        //3.发送请求
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.解析响应
        Aggregations aggregations = response.getAggregations();
        Map<String, Integer> region_counts = getAggByName(aggregations, "region_counts");
        System.out.println("展示粉丝地域分布：" + region_counts.toString());

        return region_counts;
    }

    /**
     * 构建布尔查询
     */
    private void buildBasicQuery(String writerId, Integer page, SearchRequest request) {
        // 1.构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //2.根据作家id精准查询
        boolQuery.must(QueryBuilders.matchQuery("writerId", writerId));

        //3.放入source
        request.source().query(boolQuery);
    }

    /**
     * 处理response，封装查询数据
     */
    private MyPageResult handleResponse(Integer page, Integer pageSize, SearchResponse response) {
        SearchHits searchHits = response.getHits();

        //4.1、获取总条数
        long total = searchHits.getTotalHits().value;

        // 4.2.遍历获取文档数组
        SearchHit[] hits = searchHits.getHits();
        List<FansEo> fansEos = new ArrayList<>();
        for(SearchHit hit : hits){
            // 4.2.1、获取文档sourse
            String json = hit.getSourceAsString();
            // 4.2.2、反序列化
            FansEo eo = JsonUtils.jsonToPojo(json, FansEo.class);
            fansEos.add(eo);
        }

        // 4.3、数据封装返回
        MyPageResult res = new MyPageResult();
        res.setTotal(total);
        res.setFansEos(fansEos);

        return res;
    }

    /**
     * 根据聚合名称获取聚合结果key+val
     */
    private HashMap<String, Integer> getAggByName(Aggregations aggregations, String aggName) {
        HashMap<String, Integer> map = new HashMap<>();

        // 4.1、根据聚合名称获取聚合结果
        Terms sexCountsTerms = aggregations.get(aggName);

        // 4.2、获取buckets数组
        List<? extends Terms.Bucket> buckets = sexCountsTerms.getBuckets();
        System.out.println("buckets.size()：" + buckets.size());

        // 4.3、遍历buckets数据，获取男女分布数
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            String json = JsonUtils.objectToJson(count);
            Integer val = JsonUtils.jsonToPojo(json, Integer.class);
            map.put(key, val);
        }

        return map;
    }


}
