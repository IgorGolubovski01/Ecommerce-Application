package com.ecom.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10,20,1); // 5 requests per second with a burst capacity of 10
    }

    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono
                .just(exchange
                        .getRequest()
                        .getRemoteAddress()
                        .getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("product", r -> r
                        .path("/api/products/**")
                        .filters(f->f
                                .retry(retryConfig -> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET)
                                )
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())
                                )
                                .circuitBreaker(config -> config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/products")
                        ))

//                        .filters(f -> f.rewritePath("/products(?<segment>/?.*)",
//                                "/api/products${segment}"))
                        .uri("lb://PRODUCT"))

                .route("user", r -> r
                        .path("/api/user/**")
//                        .filters(f -> f.rewritePath("/user(?<segment>/?.*)",
//                                "/api/user${segment}"))
                        .uri("lb://USER"))

                .route("order", r -> r
                        .path("/api/orders/**","/api/cart/**")
//                        .filters(f -> f.rewritePath("/(?<segment>/?.*)",
//                                "/api/${segment}"))
                        .uri("lb://ORDER"))

                .route("eureka", r -> r
                        .path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))

                .route("eureka-server-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}
