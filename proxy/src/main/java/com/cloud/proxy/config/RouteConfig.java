package com.cloud.proxy.config;

import com.cloud.proxy.filter.RequestBodyValidation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.UUID;

@Configuration
@EnableConfigurationProperties(UriConfiguration.class)
public class RouteConfig {

    private final RequestBodyValidation validationFilter;

    public RouteConfig(RequestBodyValidation validationFilter) {
        this.validationFilter = validationFilter;
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {

        String httpUri = uriConfiguration.getBackend();
        UUID traceID = UUID.randomUUID();
        return builder.routes()
                .route(p -> p
                        .path("/users/create")
                        //For local testing
//                        .path("/post").and().method(HttpMethod.POST)
                        .filters(f -> f
                                .addRequestHeader("X-Trace-Id", traceID.toString())
//                                .filters(validationFilter)
                        )
                        .uri(httpUri))
                .route(p -> p
                        .path("/users/{id}")
                        //For local testing
//                        .path("/get").and().method(HttpMethod.GET)
                        .filters(f -> f
                                        .addRequestHeader("X-Trace-Id", traceID.toString())
                        )
                        .uri(httpUri))
                .build();
    }

}
