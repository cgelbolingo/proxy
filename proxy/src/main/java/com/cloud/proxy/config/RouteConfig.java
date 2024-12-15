package com.cloud.proxy.config;

import com.cloud.proxy.filter.RequestBodyValidation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

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
                        .path("/users/create").and().method(HttpMethod.POST)
//                        For local testing
//                        .path("/post").and().method(HttpMethod.POST)
                        .filters(f -> f
                                .addRequestHeader("X-Trace-Id", traceID.toString())
//                                .filters(validationFilter)
                        )
                        .uri(httpUri))
                .route(p -> p
                        .path("/users/{id}").and().method(HttpMethod.GET)
//                        For local testing
//                        .path("/get").and().method(HttpMethod.GET)
                        .filters(f -> f
                                        .addRequestHeader("X-Trace-Id", traceID.toString())
                        )
                        .uri(httpUri))

                .route(p -> p
                        .path("/users/{id}/update").and().method(HttpMethod.PUT)
//                        For local testing
//                        .path("/put").and().method(HttpMethod.PUT)
                        .filters(f -> f
                                .modifyRequestBody(String.class, String.class, (exchange, body) -> {
                                    exchange.getRequest().mutate()
                                            .method(HttpMethod.POST);
                                    //For local testing
//                                    exchange.getRequest().mutate().path("/post");
                                    return Mono.just(body);
                                })
                                .addRequestHeader("X-Trace-Id", traceID.toString())
                        )
                        .uri(httpUri))
                .route(p -> p
                        .path("/users/{id}/delete").and().method(HttpMethod.DELETE)
//                        For local testing
//                        .path("/delete").and().method(HttpMethod.DELETE)
                        .filters(f -> f
                                .addRequestHeader("X-Trace-Id", traceID.toString())
                                .filter((exchange, chain) -> {
                                    exchange.getRequest().mutate()
                                            .method(HttpMethod.POST)
                                            .build();
                                    return chain.filter(exchange);
                                })
                        )
                        .uri(httpUri))
                .build();
    }

}
