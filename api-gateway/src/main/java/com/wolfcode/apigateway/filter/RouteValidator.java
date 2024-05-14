package com.wolfcode.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/user/auth/sign-up",
            "/user/auth/login",
            "/user/auth/forgot-password",
            "/user/auth/reset-password/",
            "/api/v1/events",
            "/eureka/**"
    );

    public Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
