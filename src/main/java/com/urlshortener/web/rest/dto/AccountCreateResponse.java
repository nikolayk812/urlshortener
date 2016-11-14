package com.urlshortener.web.rest.dto;

import java.util.Objects;

import static com.urlshortener.util.Constants.ACCOUNT_CREATE_FAILURE_DESCRIPTION;

public class AccountCreateResponse {
    public static final String SUCCESS_DESCRIPTION = "Your account is opened";
    private static final AccountCreateResponse FAILURE = new AccountCreateResponse(null, false, ACCOUNT_CREATE_FAILURE_DESCRIPTION);

    private boolean success;
    private String description;
    private String password;

    public AccountCreateResponse() {
    }

    public AccountCreateResponse(String password, boolean success, String description) {
        this.success = success;
        this.description = description;
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static AccountCreateResponse success(String password) {
        Objects.requireNonNull(password);
        return new AccountCreateResponse(password, true, SUCCESS_DESCRIPTION);
    }

    public static AccountCreateResponse failure() {
        return FAILURE;
    }
}
