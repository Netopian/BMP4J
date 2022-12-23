package netopian.network.protocol.bmp4j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerUIConfig {
    /**
     * @description: API Infos for swagger http://localhost:8080/swagger-ui.html#/
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.huawei.network.protocol.bmp4j.controller"))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("bmp4j - huawei network protocol")
            .description("you know, to simplify protocol")
            .termsOfServiceUrl("")
            .version("1.0")
            .contact(new Contact("l30010062", "https://codehub-y.huawei.com/to/do", "liujinliang10@huawei.com"))
            .build();
    }
}
