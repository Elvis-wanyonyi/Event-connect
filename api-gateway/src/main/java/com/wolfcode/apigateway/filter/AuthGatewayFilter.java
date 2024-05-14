package com.wolfcode.apigateway.filter;

import com.wolfcode.apigateway.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthGatewayFilter extends AbstractGatewayFilterFactory<AuthGatewayFilter.Config> {

    private final RouteValidator routeValidator;
    private final JwtUtil jwtUtil;


    public AuthGatewayFilter(RouteValidator routeValidator, JwtUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
                if (authHeaders == null || authHeaders.isEmpty()) {
                    return Mono.error(new RuntimeException("Authorization header missing"));
                }

                String authHeader = authHeaders.get(0);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return Mono.error(new RuntimeException("Invalid Authorization header"));
                }

                String token = authHeader.substring(7);

                try {
                    jwtUtil.isTokenValid(token);
                    List<String> userRoles = jwtUtil.extractUserRoles(token);
                    exchange.getRequest().mutate().header("userRoles", String.join(",", userRoles));
                } catch (Exception e) {
                    throw new RuntimeException(e.getLocalizedMessage());
                }

            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
