package com.example.demo.exceptions;

import com.example.demo.exceptions.validation.ErrorMessageFields;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;



@RestControllerAdvice
public class ApiHandlerExceptions {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException exception, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception, exchange.getRequest().getURI().getPath()));
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public Object exception(ServerWebExchange exchange, Exception exception) {
        return new ErrorMessage(exception,  exchange.getRequest().getURI().getPath());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessageFields> handleBindException(WebExchangeBindException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(validacionesDatos(ex, exchange));
    }

    private ErrorMessageFields validacionesDatos(WebExchangeBindException ex, ServerWebExchange exchange) {
        return new ErrorMessageFields(ex, exchange.getRequest().getURI().getPath());
    }
}