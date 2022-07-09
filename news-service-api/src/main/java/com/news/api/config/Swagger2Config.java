package com.news.api.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description Swagger2配置类
 * @create 2022-06-15-10:23
 */

@Configuration
@EnableSwagger2
public class Swagger2Config {

    //    http://localhost:8003/swagger-ui.html     原路径
    //    http://win.news.com:8003/doc.html     原路径

    // 配置swagger2核心配置 docket

    @Bean
    public Docket createRestApi() {

        Predicate<RequestHandler> adminPredicate = RequestHandlerSelectors.basePackage("com.news.admin.controller");
        Predicate<RequestHandler> articlePredicate = RequestHandlerSelectors.basePackage("com.news.article.controller");
        Predicate<RequestHandler> userPredicate = RequestHandlerSelectors.basePackage("com.news.user.controller");
        Predicate<RequestHandler> filesPredicate = RequestHandlerSelectors.basePackage("com.news.files.controller");

        return new Docket(DocumentationType.SWAGGER_2)  // 指定api类型为swagger2
                .apiInfo(apiInfo())                     // 用于定义api文档汇总信息
                .select()
//                .apis(Predicates.or(userPredicate))
                .apis(Predicates.or(adminPredicate, articlePredicate, userPredicate, filesPredicate))
                .paths(PathSelectors.any())             // 所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("新闻·自媒体接口api")                       // 文档页标题
                .description("专为新闻·自媒体平台提供的api文档")     // 详细信息
                .version("1.0")                                 // 文档版本号
                .build();
    }
}
