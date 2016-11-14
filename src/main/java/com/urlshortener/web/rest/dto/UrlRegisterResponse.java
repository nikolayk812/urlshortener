package com.urlshortener.web.rest.dto;

public class UrlRegisterResponse {
    private String shortUrl;

    public UrlRegisterResponse() {
    }

    public UrlRegisterResponse(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }


}
