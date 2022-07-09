package com.news.elasticsearch.constants;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 创建article索引库 DSL
 * @create 2022-07-02-14:15
 */
public class ArticleConstants {
    public static final String MAPPING_ARTICLE = "{\n" +
            "  \"mappings\":{\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "            \"type\": \"text\"\n" +
            "        },\n" +
            "        \"title\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\"\n" +
            "        },\n" +
            "        \"categoryId\": {\n" +
            "            \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"articleType\": {\n" +
            "            \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"articleCover\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"publishUserId\": {\n" +
            "            \"type\": \"text\"\n" +
            "        },\n" +
            "        \"publishTime\": {\n" +
            "            \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
