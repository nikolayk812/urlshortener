package com.urlshortener.service;

import com.urlshortener.model.Account;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repo.AccountRepository;
import com.urlshortener.repo.UrlMappingRepository;
import com.urlshortener.service.exceptions.AccountDuplicateException;
import com.urlshortener.service.exceptions.AccountNotFoundException;
import com.urlshortener.service.exceptions.TargetUrlDuplicateException;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.urlshortener.util.Constants.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final static int MAX_GENERATION_ATTEMPTS = 10;

    private final AccountRepository accountRepo;
    private final UrlMappingRepository urlMappingRepo;
    private final UrlHitCountingCache cache;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo, UrlMappingRepository urlMappingRepo, UrlHitCountingCache cache) {
        this.accountRepo = accountRepo;
        this.urlMappingRepo = urlMappingRepo;
        this.cache = cache;
    }


    @Override
    public Account createAccount(String accountName) {
        accountRepo.findOneByName(accountName).ifPresent(account -> {
            throw new AccountDuplicateException(ACCOUNT_CREATE_FAILURE_DESCRIPTION, accountName);
        });

        String password = RandomStringGenerator.generate(PASSWORD_LENGTH);
        //it is possible to get same password for different accounts, but it is okay
        Account account = new Account(accountName, password);
        return accountRepo.save(account);
    }

    @Override
    public String registerUrl(String targetUrl, RedirectType redirectType, String accountName) {
        Account account = accountRepo.findOneByName(accountName)
                .orElseThrow(() -> new AccountNotFoundException("Cannot register target URL: " + targetUrl, accountName));

        UrlMapping urlMapping;
        String shortUrl;
        Optional<UrlMapping> urlMappingOptional = urlMappingRepo.findByTargetUrlAndRedirectType(targetUrl, redirectType);
        if (urlMappingOptional.isPresent()) {
            urlMapping = urlMappingOptional.get();
            Optional<String> accountNameOptional = urlMapping.getAccounts().stream()
                    .map(Account::getName)
                    .filter(an -> an.equals(accountName))
                    .findAny();
            if (accountNameOptional.isPresent()) {
                throw new TargetUrlDuplicateException("Duplicate url", targetUrl);
            } else {
                urlMapping.getAccounts().add(account);
                shortUrl = urlMapping.getShortUrl();
            }
        } else {
            //it is possible to get duplicate short URL generated
            int attempts = 0;
            do {
                attempts++;
                if (attempts >= MAX_GENERATION_ATTEMPTS)
                    throw new TargetUrlDuplicateException("Failed to generate unique short URL within " +
                            MAX_GENERATION_ATTEMPTS + " attempts", targetUrl);
                shortUrl = RandomStringGenerator.generate(SHORT_URL_LENGTH);
            } while (urlMappingRepo.findByShortUrl(shortUrl).isPresent());
            urlMapping = new UrlMapping(shortUrl, targetUrl, redirectType, singletonList(account));
        }

        urlMappingRepo.save(urlMapping);
        return shortUrl;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Map<String, Integer> getUrlRedirectStats(String accountName) {
        Account account = accountRepo.findOneByName(accountName)
                .orElseThrow(() -> new AccountNotFoundException("Can not get URL statistics", accountName));

        return urlMappingRepo.findByAccounts(singletonList(account))
                .stream()
                .map(UrlMapping::getTargetUrl)
                .distinct()
                .collect(toMap(
                        Function.identity(),
                        cache::getCount));
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
