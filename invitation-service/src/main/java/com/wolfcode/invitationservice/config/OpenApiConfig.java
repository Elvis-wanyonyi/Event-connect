package com.wolfcode.invitationservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Api docs for user Invitations",
                version = "1.0"
        ),
        servers = { @Server(
                url = "http://localhost:8003/api/v1/invite/**"
        ) }
)
public class OpenApiConfig {

}
