package com.example.demo.repository;

import com.example.demo.models.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RepositoryProduct extends ReactiveMongoRepository<Product, String> {
}
