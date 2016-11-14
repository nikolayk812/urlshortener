package com.urlshortener.repo;

import com.urlshortener.model.Account;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Integer> {

    List<UrlMapping> findByAccounts(List<Account> accounts);

    Optional<UrlMapping> findByShortUrl(String shortUrl);

    Optional<UrlMapping> findByTargetUrlAndRedirectType(String targetUrl, RedirectType redirectType);

}
