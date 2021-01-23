package com.orvillex.reactordemo.rest;

import com.orvillex.reactordemo.domain.Taco;
import com.orvillex.reactordemo.repository.mongodb.TacoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class TacoRouteFunctionConfig {
    @Autowired
    private TacoRepository repository;
    
    @Bean
    public RouterFunction<?> routerFunctions() {
        return RouterFunctions.route(RequestPredicates.GET("/taco"), this::getAll).
            andRoute(RequestPredicates.POST("/taco"), this::save);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
            .body(repository.findAll(), Taco.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Taco> taco = request.bodyToMono(Taco.class);
        Mono<Taco> saved = repository.save(taco.block());
        return ServerResponse.ok()
            .body(saved, Taco.class);
    }
}
