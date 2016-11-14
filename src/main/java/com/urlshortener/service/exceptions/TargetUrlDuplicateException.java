package com.urlshortener.service.exceptions;

public class TargetUrlDuplicateException extends RuntimeException {
    private final String targetUrl;

    public TargetUrlDuplicateException(String message, String targetUrl) {
        super(message);
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }
}
