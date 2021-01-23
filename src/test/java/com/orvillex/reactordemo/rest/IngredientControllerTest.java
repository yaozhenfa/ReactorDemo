package com.orvillex.reactordemo.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orvillex.reactordemo.domain.Ingredient;
import com.orvillex.reactordemo.enums.Type;
import com.orvillex.reactordemo.repository.mysql.IngredientRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class IngredientControllerTest {
    @Test
    public void shouldReturnRecentIngredient() {
        Ingredient[] ingredients = {
            new Ingredient(1l, "First", Type.SAUCE),
            new Ingredient(2l, "Second", Type.VEGGIES),
            new Ingredient(3l, "Sefee", Type.WRAP)
        };

        Flux<Ingredient> ingredientFlux = Flux.just(ingredients);

        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        when(repo.findAll()).thenReturn(ingredientFlux);

        WebTestClient testClient = WebTestClient.bindToController(
            new IngredientController(repo)).build();
        
        testClient.get().uri("/ingredient")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$").isNotEmpty()
            .jsonPath("$[0].id").isEqualTo(ingredients[0].getId().toString());

        testClient.get().uri("/ingredient")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Ingredient.class)
            .contains(ingredients);
    }

    @Test
    public void shouldSaveIngredient() {
        Ingredient ingredient = new Ingredient(1l, "First", Type.SAUCE);
        Mono<Ingredient> monoIngredient = Mono.just(ingredient);

        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        when(repo.save(any())).thenReturn(monoIngredient);

        WebTestClient testClient = WebTestClient.bindToController(
            new IngredientController(repo)).build();

        testClient.post().uri("/ingredient")
            .contentType(MediaType.APPLICATION_JSON)
            .body(monoIngredient, Ingredient.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Ingredient.class)
            .isEqualTo(ingredient);
    }
}
