package kz.samat.apigatewayservice.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        log.info("Incoming request, " + exchange.getRequest().getURI());
        HttpHeaders headers = exchange.getRequest().getHeaders();
        exchange.getRequest().mutate()
                .header("userId", "")
                .header("role", "")
                .build();

        if (headers.containsKey("Authorization")) {
            final String token = headers.getOrEmpty("Authorization").get(0);
            String[] split = token.split(" ");

            if (!jwtUtils.validateJwtToken(split[1])) {
                return this.onError(exchange);
            }

            Claims claims = jwtUtils.getAllClaimsFromToken(split[1]);
            String role = null;

            if (claims.get("role") instanceof Map<?, ?>) {
                Map<String, String> roles = (Map<String, String>) claims.get("role");

                if (!roles.isEmpty()) {
                    role = roles.entrySet().iterator().next().getValue();
                }
            }

            exchange.getRequest().mutate()
                    .header("userId", String.valueOf(claims.getSubject()))
                    .header("role", role)
                    .build();
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }
}
