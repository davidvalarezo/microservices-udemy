package com.udemy.springcloud.msvc.products.repositories;

import org.springframework.data.repository.CrudRepository;

import com.udemy.libs.msvc.commons.entities.Product;


public interface ProductRepository extends CrudRepository<Product, Long> {

}
