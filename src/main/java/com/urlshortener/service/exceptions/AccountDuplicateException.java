package com.urlshortener.service.exceptions;

public class AccountDuplicateException extends RuntimeException {
    private final String accountName;

    public AccountDuplicateException(String message, String accountName) {
        super(message);
        this.accountName = accountName;
    }

    public AccountDuplicateException(String message, Throwable cause, String accountName) {
        super(message, cause);
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }
}
