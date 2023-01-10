package kz.samat.apigatewayservice.config;

import kz.samat.apigatewayservice.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableHystrix
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ref-data-service", r -> r.path("/ref-data-service/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://REF-DATA-SERVICE"))
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://USER-SERVICE"))
                .route("patient-service", r -> r.path("/patient-service/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://PATIENT-SERVICE"))
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).build();
    }
}
