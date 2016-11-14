package com.urlshortener.web.rest.dto;

public class AccountCreateRequest {
    private String accountId;

    public AccountCreateRequest() {
    }

    public AccountCreateRequest(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
