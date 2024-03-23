package co.syngleton.chartomancer.config_controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/config")
@Validated
@Scope("request")
@AllArgsConstructor
public class ConfigController {

    @GetMapping("/get-user-validation-data")
    ResponseEntity<UserValidationDataDTO> getUserValidationData() {
        return new ResponseEntity<>(UserValidationDataDTO.fetch(),HttpStatus.OK);
    }

}
