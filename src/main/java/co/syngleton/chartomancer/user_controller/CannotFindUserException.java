package co.syngleton.chartomancer.user_controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CannotFindUserException extends RuntimeException {
    public CannotFindUserException(String message) {
        super(message);
        log.error(message);
    }
}