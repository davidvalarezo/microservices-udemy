package com.udemy.springcloud.msvc.items.services;

import java.util.List;
import java.util.Optional;

import com.udemy.libs.msvc.commons.entities.Product;
import com.udemy.springcloud.msvc.items.models.Item;


public interface ItemService {
    List<Item> findAll();
    Optional<Item> findById(Long id);

    Product save(Product product);
    Product update(Product product, Long id);
    void deleteById(Long id);

}
