package com.example.demo.service.impl;

import com.example.demo.dtos.ProductCreateDto;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.models.Product;
import com.example.demo.repository.RepositoryProduct;
import com.example.demo.service.ServiceProduct;
import com.example.demo.utils.CopyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ServiceProductImp implements ServiceProduct {

    private final RepositoryProduct repositoryProduct;
    private final CopyProperties copyProperties;
    private static String notFoundMessage = "Product id ( %s )";
    @Override
    public Flux<Product> findAll() {
        return repositoryProduct.findAll().map(product-> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Mono<Product> findById(String id) {
        return repositoryProduct.
                findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format(notFoundMessage, id))))
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });
    }

    @Override
    public Mono<Product> save(ProductCreateDto productDto) {
        Product product = new Product();
        copyProperties.copyProperties(productDto, product);
        product.setCreatedAt(new Date());
        return repositoryProduct.save(product);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repositoryProduct.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repositoryProduct.deleteAll();
    }
}
