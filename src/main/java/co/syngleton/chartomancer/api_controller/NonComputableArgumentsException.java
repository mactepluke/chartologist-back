package co.syngleton.chartomancer.api_controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
class NonComputableArgumentsException extends RuntimeException {
    NonComputableArgumentsException(String message) {
        super(message);
    }
}
