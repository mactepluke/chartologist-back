package co.syngleton.chartomancer.controller.devtools;

import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.service.misc.LaunchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Log4j2
@RestController
@RequestMapping("/devtools")
@Scope("request")
public class ShellController {
    @Value("${root_password}")
    private String rootPassword;

    private final LaunchService launchService;
    private final DataController dataController;
    private final PatternController patternController;
    private final CoreData coreData;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ShellController(DataController dataController,
                           PatternController patternController,
                           LaunchService launchService,
                           CoreData coreData,
                           PasswordEncoder passwordEncoder) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.launchService = launchService;
        this.coreData = coreData;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/launch-shell/{password}")
    public ResponseEntity<Boolean> launchShell(@PathVariable String password) {

        HttpStatus status;
        boolean result = false;

        if (passwordEncoder.matches(password, rootPassword)) {
            status = OK;
            result = launchService.launchShell(dataController, patternController, coreData);
        } else {
            log.error("Invalid password for devToolsUser.");
            status = UNAUTHORIZED;
        }

        return new ResponseEntity<>(result, status);
    }
}