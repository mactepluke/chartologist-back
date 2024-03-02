package co.syngleton.chartomancer.api_controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Log4j2
class UnexpectedRequestResultException extends RuntimeException {
    UnexpectedRequestResultException(String message) {
        super(message);
        log.error("UnexpectedRequestResultException: " + message);
    }
}


