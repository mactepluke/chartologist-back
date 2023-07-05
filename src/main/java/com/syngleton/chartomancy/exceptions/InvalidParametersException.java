package com.syngleton.chartomancy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidParametersException extends RuntimeException {

    public InvalidParametersException(String message) {
        super(message);
    }
}
