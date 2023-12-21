package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.repository.RepositoryProduct;
import com.example.demo.service.ServiceProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.util.List;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ProductControllerTest {

    Logger logger = LoggerFactory.getLogger(ProductControllerTest.class);
    @MockBean
    private RepositoryProduct repositoryProduct;
    @Autowired
    private ServiceProduct serviceProduct;
    @Autowired
    private WebTestClient webTestClient;
    @Test
    void all() {
        List<Product> products = List.of(new Product("TV Panasonic Pantalla LCD", 456.89),
                new Product("Sony Camara HD Digital", 177.89));
        products.forEach(element->element.setId(UUID.randomUUID().toString()));


        Flux<Product> $products = Flux.fromIterable(products);
        Mockito.when(repositoryProduct.findAll()).thenReturn($products);

        String URL =  "/api"+ProductController.PRODUCT;

        webTestClient.get()
                .uri(URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .consumeWith(response->{
                    List<Product> payload = response.getResponseBody();
                    logger.info(" Response: "+payload);
                });

    }
}