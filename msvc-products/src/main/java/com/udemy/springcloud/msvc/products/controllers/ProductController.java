package com.udemy.springcloud.msvc.products.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.udemy.libs.msvc.commons.entities.Product;
import com.udemy.springcloud.msvc.products.services.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
//@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    //private final CircuitBreakerFactory cbFactory;

    public ProductController(ProductService productService //, 
    //CircuitBreakerFactory cbFactory
    ) {
        this.productService = productService;
        //this.cbFactory = cbFactory;
    }

    @GetMapping
    public ResponseEntity<List<Product>> list() {
        return ResponseEntity.ok(this.productService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        if(id.equals(10L)){
            //throw new IllegalStateException("Product not found");
        }
        if(id.equals(7L)){
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Optionally log or handle the exception
            }
        }

        Optional<Product> optionalProduct = productService.findById(id);
        if(optionalProduct.isPresent()){
            return ResponseEntity.ok(optionalProduct.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
       Optional<Product> optionalProduct = productService.findById(id);
        if(optionalProduct.isPresent()){
            Product productDB = optionalProduct.orElseThrow();
            productDB.setName(product.getName());
            productDB.setPrice(product.getPrice());
            productDB.setCreatedAt(product.getCreatedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productDB));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        //productService.deleteById(id);
        //return ResponseEntity.noContent().build();

       Optional<Product> optionalProduct = productService.findById(id);
        if(optionalProduct.isPresent()){
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }   

    

}
