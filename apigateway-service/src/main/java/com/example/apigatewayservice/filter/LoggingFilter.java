package com.example.apigatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter gatewayFilter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (config.preLogger) {
                log.info("[LoggingFilter Pre] request code: {} | baseMessage: {}", request.getId(), config.baseMessage);
            }

            // Global Post Filter
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        if (config.postLogger) {
                            log.info("[LoggingFilter POST] response code: {} | baseMessage: {}", response.getStatusCode(), config.baseMessage);
                        }
                    }));
        }, Ordered.HIGHEST_PRECEDENCE);

        return gatewayFilter;

//        // Logging Pre Filter
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            if (config.preLogger) {
//                log.info("[LoggingFilter Pre] request code: {} | baseMessage: {}", request.getId(), config.baseMessage);
//            }
//
//            // Logging Post Filter
//            return chain.filter(exchange)
//                    .then(Mono.fromRunnable(() -> {
//                        if (config.postLogger) {
//                            log.info("[LoggingFilter POST] response code: {} | baseMessage: {}", response.getStatusCode(), config.baseMessage);
//                        }
//                    }));
//        };
    }
}
