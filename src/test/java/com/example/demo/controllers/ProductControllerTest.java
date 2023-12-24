package com.example.demo.controllers;

import com.example.demo.dtos.ProductCreateDto;
import com.example.demo.models.Product;
import com.example.demo.repository.RepositoryProduct;
import com.example.demo.service.ServiceProduct;
import com.example.demo.utils.CopyProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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
    @Autowired
    private CopyProperties copyProperties;
    private String URL = "/api"+ProductController.PRODUCT;
    @Test
    void all() {
        List<Product> products = List.of(new Product("TV Panasonic Pantalla LCD", 456.89),
                new Product("Sony Camara HD Digital", 177.89));
        products.forEach(element->element.setId(UUID.randomUUID().toString()));


        Flux<Product> $products = Flux.fromIterable(products);
        Mockito.when(repositoryProduct.findAll()).thenReturn($products);

        webTestClient.get()
                .uri(URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(products.size());
    }
    @Test
    void allConsumeWith() {
        List<Product> products = List.of(new Product("TV Panasonic Pantalla LCD", 456.89),
                new Product("Sony Camara HD Digital", 177.89));
        products.forEach(element->element.setId(UUID.randomUUID().toString()));


        Flux<Product> $products = Flux.fromIterable(products);
        Mockito.when(repositoryProduct.findAll()).thenReturn($products);

        webTestClient.get()
                .uri(URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .consumeWith(response->{
                    List<Product> payloadProducts = response.getResponseBody();
                    assertEquals(products.size(), payloadProducts.size());
                });
    }
    @Test
    void show() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());


        Mono<Product> $product = Mono.just(product);
        Mockito.when(repositoryProduct.findById(product.getId())).thenReturn($product);

        String showUrl = URL + String.format("/%s", product.getId());
        webTestClient.get()
                .uri(showUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(response->{
                    Product payloadProduct = response.getResponseBody();
                    assertEquals(product.getId(), payloadProduct.getId());
                    assertEquals(product.getName(), payloadProduct.getName());
                });
    }
    @Test
    void showJsonPath() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());


        Mono<Product> $product = Mono.just(product);
        Mockito.when(repositoryProduct.findById(product.getId())).thenReturn($product);

        String showUrl = URL + String.format("/%s", product.getId());
        webTestClient.get()
                .uri(showUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(product.getId())
                .jsonPath("$.name").isEqualTo(product.getName());
    }
    @Test
    void showIDMockitoAnyString() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());


        Mono<Product> $product = Mono.just(product);
        Mockito.when(repositoryProduct.findById(Mockito.anyString())).thenReturn($product);


        String showUrl = URL + String.format("/%s", "1");
        webTestClient.get()
                .uri(showUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(response->{
                    Product payloadProduct = response.getResponseBody();
                    assertEquals(product.getId(), payloadProduct.getId());
                    assertEquals(product.getName(), payloadProduct.getName());
                });
    }
    @Test
    void showNotFound() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());


        Mono<Product> $product = Mono.just(product);
        Mockito.when(repositoryProduct.findById(product.getId())).thenReturn(Mono.empty());

        String showUrl = URL + String.format("/%s", product.getId());
        webTestClient.get()
                .uri(showUrl)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Not Found Exception (404). Product id ( "+ product.getId() + " )");
    }
    @Test
    void create() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());

        ProductCreateDto productDto = new ProductCreateDto();
        copyProperties.copyProperties(product, productDto);

        // Obtengo el valor que se guardara en la BD al llamar al metodo save dentro del repository
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(repositoryProduct.save(productCaptor.capture())).thenReturn(Mono.just(product));

        webTestClient.post()
                .uri(URL)
                .body(Mono.just(productDto), ProductCreateDto.class)
                .exchange()
                .expectStatus()
                .isCreated();

        Product captureProductSave = productCaptor.getValue();
        //Valido que el objeto que se guardara en la BD sea el mismo que envie en el DTO y que tenga el campo createdAt
        assertEquals(productDto.getName(), captureProductSave.getName());
        assertEquals(productDto.getPrice(), captureProductSave.getPrice());
        assertNotNull(captureProductSave.getCreatedAt());
    }
    @Test
    void createNameIsRequired() {
        ProductCreateDto productDto = new ProductCreateDto("",456.89);

        webTestClient.post()
                .uri(URL)
                .body(Mono.just(productDto), ProductCreateDto.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.fieldsError[0].name").isEqualTo("name")
                .jsonPath("$.fieldsError[0].rejectValue").isEqualTo("")
                .jsonPath("$.fieldsError[0].message").isEqualTo("no debe estar vacío");
    }
    @Test
    void delete() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());

        Mockito.when(repositoryProduct.deleteById(product.getId())).thenReturn(Mono.empty());

        String deleteUrl = URL + String.format("/%s", product.getId());
        webTestClient.delete()
                .uri(deleteUrl)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
        Mockito.verify(repositoryProduct).deleteById(product.getId());
    }
    @Test
    void deleteAll() {
        Product product = new Product("TV Panasonic Pantalla LCD", 456.89);
        product.setId(UUID.randomUUID().toString());

        Mockito.when(repositoryProduct.deleteAll()).thenReturn(Mono.empty());


        String deleteAllUrl = URL + "/all";
        webTestClient.delete()
                .uri(deleteAllUrl)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
        Mockito.verify(repositoryProduct).deleteAll();
    }
}