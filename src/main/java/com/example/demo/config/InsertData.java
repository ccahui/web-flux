package com.example.demo.config;

import com.example.demo.models.Product;
import com.example.demo.repository.RepositoryProduct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InsertData implements CommandLineRunner {
    private Logger log = LoggerFactory.getLogger(InsertData.class);
    private final RepositoryProduct repositoryProduct;
    @Override
    public void run(String... args) throws Exception {

        List<Product> products = List.of(new Product("TV Panasonic Pantalla LCD", 456.89),
                new Product("Sony Camara HD Digital", 177.89),
                new Product("Apple iPod", 46.89),
                new Product("Sony Notebook", 846.89),
                new Product("Hewlett Packard Multifuncional", 200.89),
                new Product("Bianchi Bicicleta", 70.89),
                new Product("HP Notebook Omen 17", 2500.89),
                new Product("Mica Cómoda 5 Cajones", 150.89),
                new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.89)
        );
        Flux<Product> $products = Flux.fromIterable(products);

        $products.flatMap(product -> repositoryProduct.save(product)).subscribe(product -> log.info("Insert: "+product));
    }
}
