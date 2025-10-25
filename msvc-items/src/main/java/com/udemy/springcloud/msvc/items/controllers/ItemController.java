package com.udemy.springcloud.msvc.items.controllers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.springcloud.msvc.items.models.Item;
import com.udemy.libs.msvc.commons.entities.Product;
import com.udemy.springcloud.msvc.items.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RefreshScope
@RestController
public class ItemController {
    private final ItemService itemService;
    private final CircuitBreakerFactory cbFactory;
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Value("${configuracion.texto}")
    private String text;

    @Autowired
    private Environment env;

    public ItemController(@Qualifier("itemServiceFeign") ItemService service,
        CircuitBreakerFactory cbFactory
        ) {
        this.itemService = service;
        this.cbFactory = cbFactory;
    }

    
    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port) {
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);
        logger.info(port);
        logger.info(text);

        if(env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")){
            json.put("author.name", env.getProperty("configuracion.autor.nombre"));
            json.put("author.email", env.getProperty("configuracion.autor.email"));
        }
        return ResponseEntity.ok(json);
    }

    @GetMapping
    public List<Item> list(@RequestParam(name="name", required = false) String name, 
            @RequestHeader(name="token-request", required = false) String token) {
                System.out.println(name);
                System.out.println(token);
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id, Integer quantity){
        Optional<Item> optionalItem = cbFactory.create("items").run(() -> itemService.findById(id), e -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            Product product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony ER");
            product.setPrice(500.00);
            return Optional.of( new Item(product, 5));

         } );          
        //itemService.findById(id);
        if (optionalItem.isPresent()) {
            return ResponseEntity.ok( optionalItem.get());
        }
        return ResponseEntity.status(404)
        .body(Collections.singletonMap("message", "Item not found"));
        //notFound().build();
    }

    @CircuitBreaker(name="items", fallbackMethod = "getFallBackMethoProduct")
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, Integer quantity){
        Optional<Item> optionalItem = itemService.findById(id);
        //itemService.findById(id);
        if (optionalItem.isPresent()) {
            return ResponseEntity.ok( optionalItem.get());
        }
        return ResponseEntity.status(404)
        .body(Collections.singletonMap("message", "Item not found"));
        //notFound().build();
    }

    public ResponseEntity<?> getFallBackMethoProduct(Throwable e){
        logger.error(e.getMessage());
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        product.setId(1L);
        product.setName("Camara Sony ER");
        product.setPrice(500.00);
        Item item = new Item(product, 5);
        return ResponseEntity.ok(item);
    }

    @CircuitBreaker(name="items", fallbackMethod = "getFallBackMethoProduct2")
    @TimeLimiter(name="items")
    @GetMapping("/detail2/{id}")
    public CompletableFuture<?> detail2(@PathVariable Long id, Integer quantity){
        return CompletableFuture.supplyAsync(() -> { 
                    Optional<Item> optionalItem = itemService.findById(id);
                    //itemService.findById(id);
                    if (optionalItem.isPresent()) {
                        return ResponseEntity.ok( optionalItem.get());
                    }
                    return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "Item not found"));
                    });

    }

    public CompletableFuture<?> getFallBackMethoProduct2(Throwable e){
         return CompletableFuture.supplyAsync(() -> { 
                logger.error(e.getMessage());
                Product product = new Product();
                product.setCreatedAt(LocalDate.now());
                product.setId(1L);
                product.setName("Camara Sony ER");
                product.setPrice(500.00);
                Item item = new Item(product, 5);
                return ResponseEntity.ok(item);
            }
        );
    }

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        
        return itemService.save(product);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public Product putMethodName(@PathVariable Long id, @RequestBody Product product) {
   
        return itemService.update(product, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.deleteById(id);
    }

    
}
