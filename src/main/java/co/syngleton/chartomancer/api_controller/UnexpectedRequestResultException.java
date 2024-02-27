package co.syngleton.chartomancer.api_controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class UnexpectedRequestResultException extends RuntimeException {
    UnexpectedRequestResultException(String message) {
        super(message);
    }
}


