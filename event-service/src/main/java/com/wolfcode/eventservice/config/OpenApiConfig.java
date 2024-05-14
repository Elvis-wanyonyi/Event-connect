package com.wolfcode.eventservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Api docs for user Event service",
                version = "1.0"
        ),
        servers = { @Server(
                url = "http://localhost:8000/api/v1/events/**"
        ) }
)
public class OpenApiConfig {

}
