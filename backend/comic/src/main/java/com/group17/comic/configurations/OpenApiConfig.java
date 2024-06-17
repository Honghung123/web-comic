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

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(
            @Value("${comic.api.document_name}") String documentName,
            @Value("${comic.api.version}") String version,
            @Value("${comic.api.description}") String description,
            @Value("${comic.api.server.local.url}") String serverUrl,
            @Value("${comic.api.server.local.description}") String serverDescription) {
        var license = new License().name("API License").url("http://group17.hcmus.edu.vn/license");
        List<Server> serverList = List.of(new Server().url(serverUrl).description(serverDescription));
        return new OpenAPI()
                .info(new Info()
                        .title(documentName)
                        .version(version)
                        .description(description)
                        .license(license))
                .servers(serverList);
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("comic-api-service")
                .packagesToScan("com.group17.comic.controllers")
                .build();
    }
}
