package com.group17.comic.configurations;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * - Sử dụng Swagger để tài liệu hóa API và covert nó thành API document ở Postman để chuyển giao theo ý 
 * của client (công việc này không phải dành cho Junior hay Senior mà là của Leader - người quản lí dự án)
 *   + Ngoài ra, sử dụng Swagger để tương tác api thông qua UI thay vì try hard trên các tool test api 
 * như Postman, ... chạy trên server local trong quá trình phát triển
 *  => Tại sao dùng "SpringDoc OpenAPI Starter WebMVC UI" thay vì "Swagger" dependency vì nó tích hợp sẵn Swagger 
 * và để tích hợp vào hệ thống mircoservice 
 * ==========================================================================================================
 * - @Bean openApi: Cấu hình các thông tin về API document
 * - @Bean groupedOpenApi: Cấu hình group API gộp nhiều OpenAPI doc vào một group duy nhất để tích hợp 
 * vào hệ thống microservice.
 */

@Configuration
public class OpenApiConfig { 

    @Bean
    public OpenAPI openApi(
        @Value("${comic.api.document_name}") String documentName,
        @Value("${comic.api.version}") String version,
        @Value("${comic.api.description}") String description,
        @Value("${comic.api.server.local.url}") String serverUrl,
        @Value("${comic.api.server.local.description}") String serverDescription
    ){
        var license = new License().name("API License").url("http://group17.hcmus.edu.vn/license");
        List<Server> serverList = List.of(new Server().url(serverUrl).description(serverDescription));
        return new OpenAPI().info(new Info().title(documentName)
                                            .version(version)
                                            .description(description)
                                            .license(license))
                            .servers(serverList)
                            // Cau hình cai nay neu can su dung token moi khi request
                            // .components(
                            //     new Components().addSecuritySchemes(
                            //         "Bearer",
                            //         new SecurityScheme()
                            //             .type(SecurityScheme.Type.HTTP)
                            //             .scheme("bearer")
                            //             .bearerFormat("JWT")
                            //     ) )
                            // .security(List.of(new SecurityRequirement().addList("bearerAuth")))
                            ;
    }

    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder()
                            .group("comic-api-service")
                            .packagesToScan("com.group17.comic.controller")
                            .build();
    }
}