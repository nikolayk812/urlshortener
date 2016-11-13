package com.urlshortener.model;

import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;

@Entity
@Table(name = "short_urls",
        uniqueConstraints = {@UniqueConstraint(name = "unique_target_url_redirect_type",
                columnNames = {"target_url", "redirect_type"})})
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "account_short_urls",
            joinColumns = @JoinColumn(name = "short_url_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<Account> accounts;

    public UrlMapping() {
    }

    public UrlMapping(String shortUrl, String targetUrl, RedirectType redirectType, List<Account> accounts) {
        this.shortUrl = shortUrl;
        this.targetUrl = targetUrl;
        this.redirectType = redirectType;
        this.accounts = accounts;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    //TODO: equals, hashCode, toString
}
