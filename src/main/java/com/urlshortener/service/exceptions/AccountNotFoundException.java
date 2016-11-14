package com.urlshortener.service.exceptions;


public class AccountNotFoundException extends RuntimeException {
    private final String accountName;

    public AccountNotFoundException(String message, String accountName) {
        super(message);
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

}
