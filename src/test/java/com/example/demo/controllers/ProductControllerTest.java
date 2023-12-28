package com.example.demo.controllers;

import com.example.demo.dtos.ProductCreateDto;
import com.example.demo.models.Product;
import com.example.demo.repository.RepositoryProduct;
import com.example.demo.service.ServiceProduct;
import com.example.demo.utils.CopyProperties;
import com.example.demo.utils.JsonConverterUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Locale;
import java.util.TimeZone;

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
    private JsonConverterUtil jsonConverterUtil;
    private String URL = "/api"+ProductController.PRODUCT;
    @BeforeAll
    public static void setUp() {
        // Configurar el idioma deseado para las pruebas (español en este ejemplo debido a que los mensajes de validacion 400 lo esperamos en español)
        Locale.setDefault(new Locale("es", "PE"));
        System.out.println("Configuración regional Nueva: " + Locale.getDefault());
        System.out.println("Configuración de la zona horaria Nueva: " + TimeZone.getDefault().getID());
    }
    @Test
    void all() {
        List<Product> products = jsonConverterUtil.readJsonFile(URL + "/products.json", new TypeReference<List<Product>>() {});

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
        List<Product> products = jsonConverterUtil.readJsonFile(URL + "/products.json", new TypeReference<List<Product>>() {});

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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});
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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});

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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});

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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});

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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});
        ProductCreateDto productDto = jsonConverterUtil.readJsonFile(URL+"/productCreateDto.json", new TypeReference<ProductCreateDto>(){});

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
        System.out.println("Configuración regional actual: " + Locale.getDefault());
        System.out.println("Configuración de la zona horaria actual: " + TimeZone.getDefault().getID());

        ProductCreateDto productDto = jsonConverterUtil.readJsonFile(URL+"/productCreateDtoNameEmpty.json", new TypeReference<ProductCreateDto>(){});

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
        Product product = jsonConverterUtil.readJsonFile(URL + "/product.json", new TypeReference<Product>() {});

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