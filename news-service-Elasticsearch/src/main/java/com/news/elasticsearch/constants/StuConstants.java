package com.news.elasticsearch.constants;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 测试创建stu索引库
 * @create 2022-07-02-12:25
 */
public class StuConstants {
    public static final String MAPPING_STU = "{\n" +
            "  \"mappings\":{\n" +
            "      \"properties\": {\n" +
            "        \"stuId\": {\n" +
            "            \"type\": \"long\"\n" +
            "        },\n" +
            "        \"name\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\"\n" +
            "        },\n" +
            "        \"age\": {\n" +
            "            \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"money\": {\n" +
            "            \"type\": \"float\"\n" +
            "        },\n" +
            "        \"desc\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
