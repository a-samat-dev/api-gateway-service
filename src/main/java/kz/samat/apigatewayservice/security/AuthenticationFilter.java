package kz.samat.apigatewayservice.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtils jwtUtils;
    private Set<String> openApiEndpoints;

    @Value("${api.allowedPaths}")
    private String[] apis;

    @PostConstruct
    void postConstruct() {
        openApiEndpoints = new HashSet<>(List.of(apis));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (openApiEndpoints.contains(path) || isApiDocUrl(path) | isActuatorUrl(path)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();

        if (headers.containsKey("userId") && headers.containsKey("email") && headers.containsKey("role")) {
            return chain.filter(exchange);
        }

        if (!headers.containsKey("Authorization"))
            return this.onError(exchange, "Authorization header is missing in request",
                    HttpStatus.UNAUTHORIZED);

        final String token = headers.getOrEmpty("Authorization").get(0);
        String[] split = token.split(" ");

        if (!jwtUtils.validateJwtToken(split[1]))
            return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);

        Claims claims = jwtUtils.getAllClaimsFromToken(split[1]);
        String role = null;

        if (claims.get("role") instanceof Map<?, ?>) {
            Map<String, String> roles = (Map<String, String>) claims.get("role");
            if (roles.size() != 0)
                role = roles.entrySet().iterator().next().getValue();
        }

        exchange.getRequest().mutate()
                .header("userId", String.valueOf(claims.getSubject()))
                .header("role", role)
                .build();

        return chain.filter(exchange);
    }

    private boolean isApiDocUrl(String path) {
        return path.contains("/swagger") || path.contains("/api-docs");
    }

    private boolean isActuatorUrl(String path) {
        return path.contains("/actuator");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
