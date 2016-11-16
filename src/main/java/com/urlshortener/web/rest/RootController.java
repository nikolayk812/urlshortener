package com.urlshortener.web.rest;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;


@Controller
public class RootController {
    private static final String SHORT_URL = "shortUrl";
    private static final String SHORT_URL_REGEX = "/{" + SHORT_URL + ":[a-zA-Z0-9]{" + SHORT_URL_LENGTH + "}}";
    private static final String INDEX = "index";

    private final UrlService urlService;

    @Autowired
    public RootController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/")
    public String index() {
        return INDEX;
    }

    @GetMapping(value = SHORT_URL_REGEX)
    public ModelAndView redirect(@PathVariable(SHORT_URL) String shortUrl) {
        UrlMapping urlMapping = urlService.hitShortUrl(shortUrl);
        RedirectView redirectView = new RedirectView(urlMapping.getTargetUrl());
        redirectView.setStatusCode(HttpStatus.valueOf(urlMapping.getRedirectType().getCode()));
        return new ModelAndView(redirectView);
    }
}
