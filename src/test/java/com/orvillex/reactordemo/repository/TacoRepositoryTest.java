package com.orvillex.reactordemo.repository;

import com.orvillex.reactordemo.domain.Taco;
import com.orvillex.reactordemo.repository.mongodb.TacoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class TacoRepositoryTest {
    private String id1;
    private String id2;

    @Autowired
    TacoRepository repository;

    @BeforeEach
    public void setUp() {
        this.id1 = repository.save(new Taco(null, "Mysql", "MySql Remark")).block().getId();
        this.id2 = repository.save(new Taco(null, "Mongodb", "Mongodb Remark")).block().getId();
    }

    @Test
    public void readsCountCorrectly() {
        StepVerifier.create(repository.count())
            .expectNext(2l)
            .verifyComplete();
    }

    @Test
    public void readsFirstEntityCorrectly() {
        StepVerifier.create(repository.findById(Mono.just(this.id1)))
            .expectNext(new Taco(id1, "Mysql", "MySql Remark"))
            .verifyComplete();
    }

    @Test
    public void readsSomeEntitiesCorrectly() {
        StepVerifier.create(repository.findAllById(Flux.just(id1, id2)))
            .expectNext(new Taco(id1, "Mysql", "MySql Remark"), new Taco(id2, "Mongodb", "Mongodb Remark"))
            .verifyComplete();
    }

    @Test
    public void saveEntityCorrectly() {
        StepVerifier.create(repository.saveAll(Flux.just(new Taco(null, "Redis", "Redis Marker"))))
        .expectNext(new Taco(null, "Redis", "Redis Marker"))    
        .verifyComplete();
    }

    @Test
    public void updateEntityCorrectly() {
        StepVerifier.create(repository.saveAll(Flux.just(new Taco(id1, "SQLSERVER", "SQLSERVER Marker"))))
            .expectNext(new Taco(id1, "SQLSERVER", "SQLSERVER Marker"))
            .verifyComplete();
    }

    @Test
    public void deleteEntityCorrectly() {
        StepVerifier.create(repository.deleteById(Flux.just(id1)))
            .verifyComplete();
    }

    @Test
    public void readsByNameCorrectly() {
        StepVerifier.create(repository.findByNameOrderByRemark("Mysql", PageRequest.of(0, 1)).map(x -> x.getName()))
            .expectNext("Mysql")
            .verifyComplete();
    }

    @Test
    public void readsByRemarkCorrectly() {
        StepVerifier.create(repository.findByRemarkContaining("MySql", Sort.by("id").descending()).map(x -> x.getName()).take(1l))
            .expectNext("Mysql")
            .verifyComplete();
    }

    @Test
    public void deleteByNameCorrectly() {
        StepVerifier.create(repository.deleteByName("Mysql"))
            .expectComplete();
    }
}
