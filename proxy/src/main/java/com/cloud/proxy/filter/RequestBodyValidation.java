package com.cloud.proxy.filter;
import com.cloud.proxy.model.User;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class RequestBodyValidation implements GatewayFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getRequest().getBody()
                .collectList()
                .flatMap(data -> {
                    try {
                        String body = data.stream()
                                .map(buffer -> buffer.toString(StandardCharsets.UTF_8))
                                .collect(Collectors.joining());

                        objectMapper.readValue(body, User.class);

                        return chain.filter(exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-Validated", "true")
                                        .build())
                                .build());
                    } catch (Exception e) {
                        return exchange.getResponse().setComplete();
                    }
                });
    }


}