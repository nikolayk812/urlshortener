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


@Controller
public class RootController {
    public static final String HELP = "help";
    public static final String INDEX = "index";

    private final UrlService urlService;

    @Autowired
    public RootController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/")
    public String index() {
        return INDEX;
    }

    @GetMapping(value = "/{shortUrl}")
    public ModelAndView redirect(@PathVariable("shortUrl:[a-zA-Z0-9]{6}") String shortUrl) {
        UrlMapping urlMapping = urlService.hitShortUrl(shortUrl);
        RedirectView redirectView = new RedirectView(urlMapping.getTargetUrl());
        redirectView.setStatusCode(HttpStatus.valueOf(urlMapping.getRedirectType().getCode()));
        return new ModelAndView(redirectView);
    }
}
