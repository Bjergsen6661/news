package com.news.elasticsearch.constants;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 创建fans索引库 DSL
 * @create 2022-07-04-13:10
 */
public class FansConstants {
    public static final String MAPPING_FANS = "{\n" +
            "  \"mappings\":{\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"writerId\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"fanId\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"face\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"fanNickname\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\"\n" +
            "        },\n" +
            "        \"sex\": {\n" +
            "            \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"province\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
