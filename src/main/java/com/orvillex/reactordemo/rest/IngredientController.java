package com.orvillex.reactordemo.rest;

import com.orvillex.reactordemo.domain.Ingredient;
import com.orvillex.reactordemo.repository.mysql.IngredientRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ingredient")
public class IngredientController {
    private final IngredientRepository repository;

    @GetMapping
    public Flux<Ingredient> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Ingredient> find(@PathVariable("id") Long id) {
        return repository.findById(id);
    }

    @PostMapping
    public Mono<Ingredient> save(@RequestBody Ingredient data) {
        return repository.save(data);
    }

    @DeleteMapping
    public Mono<Void> delete(@PathVariable("id") Long id) {
        return repository.deleteById(id);
    }
}
