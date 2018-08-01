package com.ilink.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableSwagger2
@ComponentScan(basePackages="com.ilink.controller")
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {//api鏂囨。瀹炰緥

        return new Docket(DocumentationType.SWAGGER_2)//鏂囨。绫诲瀷锛欴ocumentationType.SWAGGER_2
                .select()//鏋勫缓api閫夋嫨鍣?
                .apis(RequestHandlerSelectors.any())//api閫夋嫨鍣ㄩ?夋嫨api鐨勫寘
                .build()//鍒涘缓鏂囨。
                . apiInfo(apiInfo());//api淇℃伅
    }

    private ApiInfo apiInfo() {//鎺ュ彛鐨勭浉鍏充俊鎭?
        return new ApiInfoBuilder()
                .title("ILink")
                .description("Base SSM")
                .termsOfServiceUrl("http://localhost:8080/iLink/")
                .contact("XYL")
                .version("4.0")
                .build();
    }
}