package com.urlshortener.model;

//TODO: check serialization
public enum RedirectType {
    MOVED_PERMANENTLY(301),
    FOUND(302);

    private final int code;

    RedirectType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
