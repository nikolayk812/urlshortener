package com.urlshortener.repo;

import com.urlshortener.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findOneByName(String name);

}
