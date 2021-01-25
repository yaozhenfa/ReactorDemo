package com.orvillex.reactordemo.repository;

import com.orvillex.reactordemo.domain.Taco;
import com.orvillex.reactordemo.repository.mongodb.TacoRepository;

import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
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

    @Test
    public void testPass() {
        // String old = "4c2680f74bfb44a9603b0311415037002abecd6744647ae905efefb6563066d11ddb0c7bba08361568597bfb499759fedddffce361df0eadbc3252a99da57cb6";
        // String newpas = "4604ebd3fd921ea5cdc2e3cb8cf00901451271fdc6e6f9fb6703492119594a10ac3442b8abb2ffa5660a775af131d8edebc02347a5d8fe32e5454943526249a2";
        // String salt = "07t61S70u719Q600xfD0Zw06c9ki1D61";
        // String password = "s123b456e";
        // DefaultHashService defaultHashService = new DefaultHashService();
        // defaultHashService.setHashAlgorithmName("SHA-512");
        // defaultHashService.setHashIterations(1024);
        // HashRequest request = (new HashRequest.Builder()).setSalt(salt).setSource(password).build();
        // String newhash = defaultHashService.computeHash(request).toHex();
    }
}
