package com.urlshortener.service.exceptions;

public class UrlDuplicateException extends RuntimeException {
    private final String targetUrl;

    public UrlDuplicateException(String message, String targetUrl) {
        super(message);
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }
}
