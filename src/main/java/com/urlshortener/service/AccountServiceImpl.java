package com.urlshortener.service;

import com.urlshortener.model.Account;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repo.AccountRepository;
import com.urlshortener.repo.UrlRepository;
import com.urlshortener.service.exceptions.AccountDuplicateException;
import com.urlshortener.service.exceptions.NotFoundException;
import com.urlshortener.service.exceptions.UrlDuplicateException;
import com.urlshortener.util.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.urlshortener.util.Constants.*;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepo;
    private final UrlRepository urlRepo;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo, UrlRepository urlRepo) {
        this.accountRepo = accountRepo;
        this.urlRepo = urlRepo;
    }


    @Override
    public Account createAccount(String accountName) {
        accountRepo.findOneByName(accountName).ifPresent(account -> {
            throw new AccountDuplicateException(ACCOUNT_CREATE_FAILURE_DESCRIPTION, accountName);
        });

        String password = RandomStringGenerator.generate(PASSWORD_LENGTH);
        Account account = new Account(accountName, password);
        return accountRepo.save(account);
    }

    @Override
    public String registerUrl(String targetUrl, RedirectType redirectType, String accountName) {
        Account account = accountRepo.findOneByName(accountName)
                .orElseThrow(() -> new NotFoundException(accountName));

        urlRepo.findByTargetUrlAndAccount(targetUrl, account)
                .ifPresent(urlMapping -> {
                    throw new UrlDuplicateException("Duplicate url for account", targetUrl);
                });

        String shortUrl = RandomStringGenerator.generate(SHORT_URL_LENGTH);
        //TODO: it might be duplicate!!!

        UrlMapping urlMapping = new UrlMapping(targetUrl, redirectType, shortUrl, account);
        urlRepo.save(urlMapping);
        return shortUrl;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Map<String, Integer> getUrlRedirectStats(String accountName) {
        Account account = accountRepo.findOneByName(accountName)
                .orElseThrow(() -> new NotFoundException(accountName));

        List<UrlMapping> list = urlRepo.findByAccount(account);
        return list.stream().collect(
                toMap(
                        UrlMapping::getTargetUrl,
                        UrlMapping::getRedirectCounter
                )
        );
    }

    @Override
    public UrlMapping hitShortUrl(String shortUrl, String accountName) {
        if (shortUrl.length() != SHORT_URL_LENGTH)
            throw new NotFoundException(shortUrl);

        Account account = accountRepo.findOneByName(accountName).get();
        UrlMapping urlMapping = urlRepo.findByShortUrlAndAccount(shortUrl, account)
                .orElseThrow(() -> new NotFoundException(shortUrl));//TODO: expcetion of other type

        urlMapping.setRedirectCounter(urlMapping.getRedirectCounter() + 1);
        return urlRepo.save(urlMapping);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepo.findOneByName(accountName);
        return accountOptional.map(account ->
                new User(accountName, account.getPassword(), emptyList()))
                .orElseThrow(() ->
                        new UsernameNotFoundException("Account " + accountName + " does not exist"));
    }

}
