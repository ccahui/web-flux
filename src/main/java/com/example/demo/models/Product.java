package com.example.demo.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private Date createdAt;

    public Product(String name, Double price){
        this.name = name;
        this.price = price;
        this.createdAt = new Date();
    }
}
