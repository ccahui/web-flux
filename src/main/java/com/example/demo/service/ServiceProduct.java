package com.example.demo.service;

import com.example.demo.dtos.ProductCreateDto;
import com.example.demo.models.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ServiceProduct {
    Flux<Product> findAll();
    Mono<Product> findById(String id);
    Mono<Product> save(ProductCreateDto product);
    Mono<Void> deleteById(String id);
    Mono<Void> deleteAll();

}
