package by.innowise.gatewayservice.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Component
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

    private static final String FALLBACK_URI = "fallbackUri";
    private final RateLimiter rateLimiter;


    public RateLimiterGatewayFilterFactory() {
        super(Config.class);

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(3))
                .build();
        this.rateLimiter = RateLimiter.of("rate-limiter", config);
    }

    @Override
    public String name() {
        return "RateLimiter";
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of(FALLBACK_URI);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(RequestNotPermitted.class, err -> {
                    if (config.getFallbackUri() != null) {
                        exchange.getAttributes().put(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, err);
                        return handleFallback(exchange, config.getFallbackUri());
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests"));
                });
    }

    private Mono<Void> handleFallback(ServerWebExchange exchange, String fallbackUri) {
        if (fallbackUri != null) {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create(fallbackUri));
        }
        return exchange.getResponse().setComplete();
    }


    @Setter
    @Getter
    public static class Config {

        private String fallbackUri;

    }
}
