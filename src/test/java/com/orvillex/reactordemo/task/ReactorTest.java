package com.orvillex.reactordemo.task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

public class ReactorTest {
    @Test
    public void createAFluxjust() {
        Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .expectAccessibleContext();
    }

    @Test
    public void createAFluxArray() {
        String[] fruits = new String[] {"Apple", "Orange", "Grape", "Banana", "Strawberry"};
        Flux<String> fruitFlux = Flux.fromArray(fruits);
        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .expectAccessibleContext();
    }

    @Test
    public void createAFluxIterable() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Orange");
        fruits.add("Grape");
        fruits.add("Banana");
        fruits.add("Strawberry");
        Flux<String> fruitFlux = Flux.fromIterable(fruits);
        StepVerifier.create(fruitFlux)
        .expectNext("Apple")
        .expectNext("Orange")
        .expectNext("Grape")
        .expectNext("Banana")
        .expectNext("Strawberry")
        .expectAccessibleContext();
    }

    @Test
    public void mergeFluxes() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
            .delayElements(Duration.ofMillis(500));
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
            .delaySubscription(Duration.ofMillis(250))
            .delayElements(Duration.ofMillis(500));
        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);
        StepVerifier.create(mergedFlux)
            .expectNext("Garfield")
            .expectNext("Lasagna")
            .expectNext("Kojak")
            .expectNext("Lollipops")
            .expectNext("Barbossa")
            .expectNext("Apples")
            .expectComplete();
    }

    @Test
    public void zipFluxes() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");
        Flux<Tuple2<String, String>> zipFlux = Flux.zip(characterFlux, foodFlux);
        StepVerifier.create(zipFlux)
            .expectNextMatches(p ->
                p.getT1().equals("Garfield") && p.getT2().equals("Lasagna"))
            .expectNextMatches(p ->
                p.getT1().equals("Kojak") && p.getT2().equals("Lollipops"))
            .expectNextMatches(p ->
                p.getT1().equals("Barbossa") && p.getT2().equals("Apples"))
            .expectComplete();
        
        Flux<String> zipedFlux = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);
        StepVerifier.create(zipedFlux)
            .expectNext("Garfield eats Lasagna")
            .expectNext("Kojak eats Lollipops")
            .expectNext("Barbossa eats Apples")
            .expectComplete();
    }

    @Test
    public void skipFluxes() {
        Flux<String> skipFlux = Flux.just("one", "two", "skip a few", "nine", "hundred").skip(3);
        StepVerifier.create(skipFlux).expectNext("nine", "hundred").verifyComplete();

        Flux<String> skipDelayFlux = Flux.just("one", "two", "skip a few", "nine", "hundred")
            .delayElements(Duration.ofSeconds(1))
            .skip(Duration.ofSeconds(4));
        StepVerifier.create(skipDelayFlux).expectNext("nine", "hundred").verifyComplete();
    }

    @Test
    public void takFluxes() {
        Flux<String> takeFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton").take(3);
        StepVerifier.create(takeFlux).expectNext("yellow", "yosem", "grand");

        Flux<String> takeDelayFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton")
            .delayElements(Duration.ofSeconds(1))
            .take(Duration.ofMillis(3500));
        StepVerifier.create(takeDelayFlux).expectNext("yellow", "yosem", "grand").verifyComplete();
    }

    @Test
    public void filterFluxes() {
        Flux<String> filterFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton")
            .filter(p -> !p.contains("zion"));
        StepVerifier.create(filterFlux).expectNext("yellow", "yosem", "grand", "teton").verifyComplete();
    }

    @Test
    public void distinct() {
        Flux<String> distinctFlux = Flux.just("yellow", "yosem", "grand", "zion", "yellow").distinct();
        StepVerifier.create(distinctFlux).expectNext("yellow", "yosem", "grand", "zion").verifyComplete();
    }

    @Test
    public void map() {
        Flux<String> mapFlux = Flux.just("yellow", "yosem", "grand")
            .map(n -> {
                return n + "s";
            });
        StepVerifier.create(mapFlux).expectNext("yellows", "yosems", "grands").verifyComplete();

        Flux<String> flatMapFlux = Flux.just("yellow", "yosem", "grand")
            .flatMap(n -> Mono.just(n).map(p -> {
                return p + "s";
            }).subscribeOn(Schedulers.parallel())
        );
        StepVerifier.create(flatMapFlux).expectNext("yellows", "yosems", "grands").verifyComplete();
    }

    @Test
    public void buffer() {
        Flux.just("yellow", "yosem", "grand")
            .buffer(2)
            .flatMap(x ->
                Flux.fromIterable(x)
                .map(y -> y + "s")
                .subscribeOn(Schedulers.parallel())
                .log()
            ).subscribe();
    }

    @Test
    public void allAndAny() {
        Mono<Boolean> has = Flux.just("a1", "a2", "a3", "a4", "a5")
            .all(a -> a.contains("a"));
        StepVerifier.create(has).expectNext(true).verifyComplete();

        has = Flux.just("a1", "a2", "a3")
            .any(a -> a.contains("2"));
        StepVerifier.create(has).expectNext(true).verifyComplete();
    }
}
