package com.urlshortener.web.rest;


import com.urlshortener.model.Account;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.service.AccountService;
import com.urlshortener.web.rest.dto.AccountRequest;
import com.urlshortener.web.rest.dto.AccountResponse;
import com.urlshortener.web.rest.dto.StatisticsResponse;
import com.urlshortener.web.rest.dto.UrlRegisterRequest;
import com.urlshortener.web.rest.dto.UrlRegisterResponse;
import com.urlshortener.web.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class RestResource {
    public static final String ACCOUNT_PATH = "/account";
    public static final String REGISTER_URL_PATH = "/register";
    public static final String STATISTICS_PATH = "/statistics";

    @Autowired
    private AccountService service;


    //TODO: handle error

    @PostMapping(value = ACCOUNT_PATH)
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest request) {
        Account account = service.createAccount(request.getAccountId());
        return new ResponseEntity<>(
                AccountResponse.success(account.getPassword()),
                HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = REGISTER_URL_PATH)
    public ResponseEntity<UrlRegisterResponse> registerUrl(@RequestBody @Valid UrlRegisterRequest request) {
        return SecurityUtils.getCurrentAccountName()
                .map(accountName -> {
                    String shortUrl = service.registerUrl(request.getUrl(), request.getRedirectTypeOrDefault(), accountName);
                    return new ResponseEntity<>(new UrlRegisterResponse(shortUrl), HttpStatus.CREATED);
                }).orElseThrow(() -> new UsernameNotFoundException("")); //TODO:
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = STATISTICS_PATH + "/{accountId}")
    public ResponseEntity<StatisticsResponse> getUrlStatistics(@PathVariable("accountId") String accountId) {
        return SecurityUtils.getCurrentAccountName()
                .map(accountName -> {
                    if (!accountName.equals(accountId))
                        throw new UsernameNotFoundException(""); //TODO:

                    Map<String, Integer> stats = service.getUrlRedirectStats(accountName);
                    return new ResponseEntity<>(new StatisticsResponse(stats), HttpStatus.OK);
                }).orElseThrow(() -> new UsernameNotFoundException("")); //TODO:
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{shortUrl}")
    public void redirect(@PathVariable("shortUrl") String shortUrl) {
        SecurityUtils.getCurrentAccountName()
                .map(accountName -> {
                    UrlMapping urlMapping = service.hitShortUrl(shortUrl, accountName);
                    RedirectView redirectView = new RedirectView(shortUrl);
                    redirectView.setStatusCode(HttpStatus.valueOf(urlMapping.getRedirectType().getCode()));
                    return new ModelAndView(redirectView);

                }).orElseThrow(() -> new UsernameNotFoundException("")); //TODO:
    }


}