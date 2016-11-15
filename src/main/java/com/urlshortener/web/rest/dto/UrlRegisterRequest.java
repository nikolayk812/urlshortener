package com.urlshortener.web.rest.dto;

import com.urlshortener.model.RedirectType;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class UrlRegisterRequest {
    private String url;

    private Optional<RedirectType> redirectType = Optional.empty();

    public UrlRegisterRequest() {
    }

    public UrlRegisterRequest(String url) {
        this.url = url;
    }

    public UrlRegisterRequest(String url, RedirectType redirectType) {
        this.url = url;
        this.redirectType = Optional.of(redirectType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Optional<RedirectType> getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Optional<RedirectType> redirectType) {
        this.redirectType = redirectType;
    }

    public RedirectType getRedirectTypeOrDefault() {
        return redirectType.orElseGet(() -> RedirectType.FOUND);
    }

}
