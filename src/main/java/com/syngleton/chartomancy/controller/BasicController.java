package com.syngleton.chartomancy.controller;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BasicController {

    protected static final byte USER_NAME = 30;
    protected BasicController() {
    }

    protected boolean emailIsValid(String email) {

        if (!email.isEmpty() && email.length() <= 40 && email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            return true;
        } else {
            log.error("Invalid email format: must be correct and max 40 characters.");
            return false;
        }
    }

    protected boolean passwordIsValid(String password) {

        if (password.length() >= 6 && password.length() <= 20)  {
            return true;
        } else {
            log.error("Invalid password format: must be between 6-20 characters.");
            return false;
        }
    }

    protected void acknowledgeRequest(String type, String email) {
        email = email.toLowerCase();
        log.info("{} request received with email: {}", type, email);
    }

}