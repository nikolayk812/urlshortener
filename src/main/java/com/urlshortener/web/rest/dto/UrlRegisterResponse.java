package com.urlshortener.web.rest.dto;

public class UrlRegisterResponse {
    private String shortUrl;

    public UrlRegisterResponse() {
    }

    //TODO: factory method
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
