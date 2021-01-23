package com.orvillex.reactordemo.repository;

import com.orvillex.reactordemo.domain.Ingredient;
import com.orvillex.reactordemo.enums.Type;
import com.orvillex.reactordemo.repository.mysql.IngredientRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
public class IngredientRepositoryTest {
    @Autowired
    IngredientRepository repository;
    
    @Test
    public void readsCountCorrectly() {
        StepVerifier.create(repository.count())
            .expectNext(10l)
            .verifyComplete();
    }

    @Test
    public void readsFirstEntityCorrectly() {
        StepVerifier.create(repository.findById(Mono.just(1l)))
            .expectNext(new Ingredient(1l, "Flour Tortilla", Type.WRAP))
            .verifyComplete();
    }

    @Test
    public void readsSomeEntitiesCorrectly() {
        StepVerifier.create(repository.findAllById(Flux.just(1l, 2l)))
            .expectNext(new Ingredient(1l, "Flour Tortilla", Type.WRAP), new Ingredient(2l, "Corn Tortilla", Type.WRAP))
            .verifyComplete();
    }

    @Test
    public void saveEntityCorrectly() {
        StepVerifier.create(repository.saveAll(Flux.just(new Ingredient(null, "Sesa", Type.VEGGIES))))
            .expectNext(new Ingredient(11l, "Sesa", Type.VEGGIES))
            .verifyComplete();
    }

    @Test
    public void updateEntityCorrectly() {
        StepVerifier.create(repository.saveAll(Flux.just(new Ingredient(10l, "DDD", Type.SAUCE))))
            .expectNext(new Ingredient(10l, "DDD", Type.SAUCE))
            .verifyComplete();
    }

    @Test
    public void deleteEntityCorrectly() {
        repository.deleteById(Flux.just(1l)).subscribe();
        StepVerifier.create(repository.count())
        .expectNext(9l)
        .verifyComplete();
    }

    @Test
    public void readsByTypeCorrectly() {
        StepVerifier.create(repository.findByTypeOrderByName(Type.WRAP, PageRequest.of(0, 1)))
            .expectNext(new Ingredient(2l, "Corn Tortilla", Type.WRAP))
            .verifyComplete();
    }

    @Test
    public void readsByNameCorrectly() {
        StepVerifier.create(repository.findByNameContaining("sa", Sort.by("id").descending()))
            .expectNext(new Ingredient(9l, "Salsa", Type.SAUCE))
            .verifyComplete();
    }

    @Test
    public void readsByNameAndTypeCorrectly() {
        StepVerifier.create(repository.findByNameContainingAndType("sa", Type.SAUCE))
            .expectNext(new Ingredient(9l, "Salsa", Type.SAUCE))
            .verifyComplete();
    }

    @Test
    public void deleteByTypeCorrectly() {
        StepVerifier.create(repository.deleteByType(Type.WRAP))
            .expectNext(2)
            .verifyComplete();
    }
}
