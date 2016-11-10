package com.urlshortener.model;

import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;

@Entity
@Table(name = "short_urls")
public class UrlMapping {

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    private Integer id;

    @Size(min = SHORT_URL_LENGTH, max = SHORT_URL_LENGTH,
            message = "Short url should be exactly " + SHORT_URL_LENGTH + " symbols")
    @NotNull
    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @NotNull
    @URL
    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "redirect_type", nullable = false)
    private RedirectType redirectType;

    //TODO: prevent loses!!
    //TODO: move to other class
    @Column(name = "redirect_counter")
    private int redirectCounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public UrlMapping() {
    }

    public UrlMapping(String targetUrl, RedirectType redirectType, String shortUrl, Account account) {
        this.shortUrl = shortUrl;
        this.targetUrl = targetUrl;
        this.redirectType = redirectType;
        this.account = account;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public RedirectType getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(RedirectType redirectType) {
        this.redirectType = redirectType;
    }

    public int getRedirectCounter() {
        return redirectCounter;
    }

    public void setRedirectCounter(int redirectCounter) {
        this.redirectCounter = redirectCounter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    //TODO: equals, hashCode, toString
}
