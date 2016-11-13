package com.urlshortener.repo;

import com.urlshortener.model.Account;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Integer> {

    List<UrlMapping> findByAccounts(List<Account> accounts);
//
//    Optional<UrlMapping> findByTargetUrlAndAccount(String targetUrl, Account account);
//
    Optional<UrlMapping> findByShortUrl(String shortUrl);

    Optional<UrlMapping> findByTargetUrlAndRedirectType(String targetUrl, RedirectType redirectType);

}
