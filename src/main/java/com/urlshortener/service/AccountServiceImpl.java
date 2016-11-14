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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepo;
    private final UrlRepository urlRepo;
    private final UrlHitCountingCache cache;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo, UrlRepository urlRepo, UrlHitCountingCache cache) {
        this.accountRepo = accountRepo;
        this.urlRepo = urlRepo;
        this.cache = cache;
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

        UrlMapping urlMapping;
        String shortUrl;
        Optional<UrlMapping> urlMappingOptional = urlRepo.findByTargetUrlAndRedirectType(targetUrl, redirectType);
        if (urlMappingOptional.isPresent()) {
            urlMapping = urlMappingOptional.get();
            Optional<String> accountNameOptional = urlMapping.getAccounts().stream()
                    .map(Account::getName)
                    .filter(an -> an.equals(accountName))
                    .findAny();
            if (accountNameOptional.isPresent()) {
                throw new UrlDuplicateException("Duplicate url", targetUrl);
            } else {
                urlMapping.getAccounts().add(account);
                shortUrl = urlMapping.getShortUrl();
            }
        } else {
            shortUrl = RandomStringGenerator.generate(SHORT_URL_LENGTH);
            //TODO: it might be duplicate!!!
            urlMapping = new UrlMapping(shortUrl, targetUrl, redirectType, singletonList(account));
        }

        urlRepo.save(urlMapping);
        return shortUrl;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Map<String, Integer> getUrlRedirectStats(String accountName) {
        Account account = accountRepo.findOneByName(accountName)
                .orElseThrow(() -> new NotFoundException(accountName));

        List<UrlMapping> list = urlRepo.findByAccounts(singletonList(account));
        return list.stream().collect(
                toMap(
                        UrlMapping::getTargetUrl,
                        um -> cache.getCount(um.getTargetUrl())
                )
        );
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
