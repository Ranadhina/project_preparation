package com.practice.Subscription.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class WebClientConfig {

    @Bean
    @RequestScope
    public WebClient webClient(HttpServletRequest request, WebClient.Builder builder) {
        // Extract token from incoming request
        String token = request.getHeader("Authorization");

        return builder
                .defaultHeader("Authorization", token != null ? token : "")
                .build();
    }
}
