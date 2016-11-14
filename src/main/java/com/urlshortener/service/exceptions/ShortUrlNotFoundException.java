package com.urlshortener.service.exceptions;

public class ShortUrlNotFoundException extends RuntimeException {
    private final String shortUrl;

    public ShortUrlNotFoundException(String message, String shortUrl) {
        super(message);
        this.shortUrl = shortUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

}
