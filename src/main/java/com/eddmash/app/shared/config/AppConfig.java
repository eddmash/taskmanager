package com.eddmash.app.shared.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    @Valid
    private AuthConfig auth;

    @Data
    @Builder
    public static class AuthConfig {
        @NotEmpty
        private String tokenKey;
        @NotEmpty
        private String authUrl;
        private List<String> unsecureEndpoints;
        @Builder.Default
        private String tokenIntrospectionPath = "token_info";
    }

}
