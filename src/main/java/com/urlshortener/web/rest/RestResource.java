package com.urlshortener.web.rest;


import com.urlshortener.model.Account;
import com.urlshortener.service.AccountService;
import com.urlshortener.web.rest.dto.AccountCreateRequest;
import com.urlshortener.web.rest.dto.AccountCreateResponse;
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

import javax.validation.Valid;
import java.util.Map;

@RestController
public class RestResource {
    public static final String ACCOUNT_PATH = "/account";
    public static final String REGISTER_URL_PATH = "/register";
    public static final String STATISTICS_PATH = "/statistics";

    private final AccountService service;

    @Autowired
    public RestResource(AccountService service) {
        this.service = service;
    }

    @PostMapping(value = ACCOUNT_PATH)
    public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody @Valid AccountCreateRequest request) {
        Account account = service.createAccount(request.getAccountId());
        return new ResponseEntity<>(
                AccountCreateResponse.success(account.getPassword()),
                HttpStatus.CREATED);
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping(value = REGISTER_URL_PATH)
    public ResponseEntity<UrlRegisterResponse> registerUrl(@RequestBody @Valid UrlRegisterRequest request) {
        return SecurityUtils.getCurrentAccountName()
                .map(accountName -> {
                    String shortUrl = service.registerUrl(request.getUrl(), request.getRedirectTypeOrDefault(), accountName);
                    return new ResponseEntity<>(new UrlRegisterResponse(shortUrl), HttpStatus.CREATED);
                }).orElseThrow(() -> new UsernameNotFoundException("Not authorized"));
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping(value = STATISTICS_PATH + "/{accountId}")
    public ResponseEntity<StatisticsResponse> getUrlStatistics(@PathVariable("accountId") String accountId) {
        return SecurityUtils.getCurrentAccountName()
                .map(accountName -> {
                    if (!accountName.equals(accountId))
                        throw new UsernameNotFoundException("Requested statistics for other account " + accountId);

                    Map<String, Integer> stats = service.getUrlRedirectStats(accountName);
                    return new ResponseEntity<>(new StatisticsResponse(stats), HttpStatus.OK);
                }).orElseThrow(() -> new UsernameNotFoundException("Not authorized"));
    }

}
