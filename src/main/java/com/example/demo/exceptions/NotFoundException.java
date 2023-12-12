package com.example.demo.exceptions;

import lombok.EqualsAndHashCode;


public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String DESCRIPTION = "Not Found Exception (404)";

    public NotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

}