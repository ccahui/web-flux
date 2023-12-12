package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ProductCreateDto {
    @NotBlank
    public String name;
    public Double price;
}
