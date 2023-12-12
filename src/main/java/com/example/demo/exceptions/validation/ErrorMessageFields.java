package com.example.demo.exceptions.validation;

import com.example.demo.exceptions.ErrorMessage;
import lombok.Getter;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorMessageFields extends ErrorMessage {
    private static final String DESCRIPTION = "Bad Request, fields Error";

    private List<FieldError> fieldsError = new ArrayList<>();

    public ErrorMessageFields(WebExchangeBindException exception, String path) {

        super(exception.getClass().getSimpleName(), DESCRIPTION, path);
        this.setFieldsError(exception);
    }

    private void setFieldsError(WebExchangeBindException exception) {
        FieldError error;
        for (org.springframework.validation.FieldError fieldError : exception.getFieldErrors()) {
            error = new FieldError(fieldError.getField(), fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage());
            fieldsError.add(error);
        }
    }
}