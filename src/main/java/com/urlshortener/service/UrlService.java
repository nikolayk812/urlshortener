package com.urlshortener.service;


import com.urlshortener.model.UrlMapping;
import com.urlshortener.service.exceptions.ShortUrlNotFoundException;

/**
 * URL service interface.
 * All methods should not accept either return {@code null} values.
 */
public interface UrlService {

    /**
     * Hits short URL, produces side effects,
     * i.e. increments hit counter.
     *
     * @param shortUrl short URL
     * @return url mapping for this short URL
     * @throws ShortUrlNotFoundException in case short URL is not registered
     */
    UrlMapping hitShortUrl(String shortUrl) throws ShortUrlNotFoundException;

}
