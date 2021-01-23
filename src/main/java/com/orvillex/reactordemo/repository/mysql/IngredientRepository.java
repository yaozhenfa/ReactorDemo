package com.orvillex.reactordemo.repository.mysql;

import com.orvillex.reactordemo.domain.Ingredient;
import com.orvillex.reactordemo.enums.Type;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, Long> {
    Flux<Ingredient> findByTypeOrderByName(Type type, Pageable pageable);

    Flux<Ingredient> findByNameContaining(String name, Sort sort);

    Flux<Ingredient> findByNameContainingAndType(String name, Type type);

    Mono<Integer> deleteByType(Type type);
}
