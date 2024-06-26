/* Setting up CHARTOMANCY_DB */
CREATE DATABASE IF NOT EXISTS CHARTOMANCY_DB;
USE CHARTOMANCY_DB;


CREATE TABLE IF NOT EXISTS USER
(
    USER_ID    INT PRIMARY KEY AUTO_INCREMENT,
    EMAIL      VARCHAR(50) NOT NULL,
    PASSWORD   VARCHAR(60) NOT NULL,
    FIRST_NAME VARCHAR(30) NOT NULL,
    LAST_NAME  VARCHAR(30) NOT NULL,
    VERIFIED   BOOL        NOT NULL,
    ENABLED    BOOL        NOT NULL
    );

