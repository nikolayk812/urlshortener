package com.urlshortener.repo;

import com.urlshortener.model.Account;
import com.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Integer> {

    List<UrlMapping> findByAccount(Account account);

    Optional<UrlMapping> findByTargetUrlAndAccount(String targetUrl, Account account);

    //TODO: rename
    //TODO: better performance?
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UrlMapping> findByShortUrlAndAccount(String shortUrl, Account account);

}
