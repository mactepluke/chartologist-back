package co.syngleton.chartomancer.user_controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidPasswordException extends RuntimeException {

    InvalidPasswordException(String message)    {
        super(message);
        log.error(message);
    }
}
