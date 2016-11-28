package com.urlshortener.util;


/**
 * Application wide constants
 */
public final class Constants {
    public static final String ACCOUNT_NAME_REGEX = "^[A-Za-z0-9]*$";

    public static final int PASSWORD_LENGTH = 8;
    public static final int SHORT_URL_LENGTH = 6;

    public static final String SEQUENCE_START_ID_STRING = "100";
    public static final int SEQUENCE_START_ID = Integer.parseInt(SEQUENCE_START_ID_STRING);

    public static final String ACCOUNT_CREATE_FAILURE_DESCRIPTION = "Account with that ID already exists";

    private Constants() {
    }
}
