package com.orvillex.reactordemo.repository.mongodb;

import com.orvillex.reactordemo.domain.Taco;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TacoRepository extends ReactiveCrudRepository<Taco, String> {
    Flux<Taco> findByNameOrderByRemark(String name, Pageable pageable);

    Flux<Taco> findByRemarkContaining(String remark, Sort sort);

    Mono<Integer> deleteByName(String name);
}
