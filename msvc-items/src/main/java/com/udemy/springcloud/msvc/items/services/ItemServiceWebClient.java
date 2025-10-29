package com.udemy.springcloud.msvc.items.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.Builder;
//import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.udemy.springcloud.msvc.items.models.Item;
import com.udemy.libs.msvc.commons.entities.Product;

@Primary
@Service
public class ItemServiceWebClient  implements ItemService {

    private final WebClient client;



    public ItemServiceWebClient(WebClient client) {
        this.client = client;
    }

    @Override
    public List<Item> findAll() {
        return this.client
            .get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Product.class)
            .map(product -> new Item(product,  new Random().nextInt(10)+1))
            .collectList()
            .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        //try {
            return this.client
                .get()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .map(product -> new Item(product,  new Random().nextInt(10)+1))
                .blockOptional();
        // } catch (WebClientResponseException e) {
        //     return Optional.empty();
        // }

    }

    @Override
    public Product save(Product product) {
        return this.client
            .post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(product)
            .retrieve()
            .bodyToMono(Product.class)
            .block();
    }

    @Override
    public Product update(Product product, Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        return this.client
                .put()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    @Override
    public void deleteById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        this.client
            .delete()
            .uri("/{id}", params)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

}
