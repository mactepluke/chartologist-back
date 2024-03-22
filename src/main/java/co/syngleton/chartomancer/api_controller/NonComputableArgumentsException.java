package co.syngleton.chartomancer.api_controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Log4j2
class NonComputableArgumentsException extends RuntimeException {
    NonComputableArgumentsException(String message) {
        super(message);
        log.error("NonComputableArgumentsException: " + message);
    }
}
