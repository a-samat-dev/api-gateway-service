package kz.samat.apigatewayservice.config;

import kz.samat.apigatewayservice.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GatewayConfig {

    private final AuthenticationFilter filter;

    private final ServiceMapping serviceMapping;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ref-data-service", r -> r.path("/ref-data-service/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceMapping.getRefDataService()))
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceMapping.getUserService()))
                .route("patient-service", r -> r.path("/patient-service/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceMapping.getPatientService()))
                .route("schedule-service", r -> r.path("/schedule-service/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceMapping.getScheduleService()))
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).build();
    }
}
