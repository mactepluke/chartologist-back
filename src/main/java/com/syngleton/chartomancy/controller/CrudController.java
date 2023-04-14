package com.syngleton.chartomancy.controller;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class CrudController {

    protected static final int USER_NAME_MAX_LENGTH = 30;
    protected static final int EMAIL_MAX_LENGTH = 50;
    protected static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    protected static final int PASSWORD_MIN_LENGTH = 8;
    protected static final int PASSWORD_MAX_LENGTH = 30;

    protected CrudController() {
    }

    protected boolean emailIsValid(String email) {

        if (!email.isEmpty() && email.length() <= EMAIL_MAX_LENGTH && email.matches(EMAIL_REGEX)) {
            return true;
        } else {
            log.error("Invalid email format: must be correct and max {} characters.", EMAIL_MAX_LENGTH);
            return false;
        }
    }

    protected boolean passwordIsValid(String password) {

        if (password.length() >= PASSWORD_MIN_LENGTH && password.length() <= PASSWORD_MAX_LENGTH) {
            return true;
        } else {
            log.error("Invalid password format: must be between {} and {} characters.", PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
            return false;
        }
    }

    protected void acknowledgeRequest(String type, String email) {
        email = email.toLowerCase();
        log.info("{} request received with email: {}", type, email);
    }

}