package com.urlshortener.service;

import com.urlshortener.model.Account;
import com.urlshortener.model.RedirectType;
import com.urlshortener.service.exceptions.AccountDuplicateException;
import com.urlshortener.service.exceptions.UrlDuplicateException;

import java.util.Map;


/**
 * Account service interface.
 * All methods should not accept either return {@code null} values.
 */
public interface AccountService {

    /**
     * Creates account with name provided
     *
     * @param accountName account name
     * @return account
     */
    Account createAccount(String accountName) throws AccountDuplicateException;

    /**
     * Registers new target URL for redirection, short URL is created
     *
     * @param url target URL
     * @param redirectType redirect type
     * @param accountName account name owning the mapping from target to short URL
     * @return short URL created
     */
    String registerUrl(String url, RedirectType redirectType, String accountName) throws UrlDuplicateException;

    /**
     *
     * @param accountName
     * @return
     */
    //TODO: not map?
    Map<String, Integer> getUrlRedirectStats(String accountName);

}
