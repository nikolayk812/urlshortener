package com.urlshortener.service;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.repo.UrlMappingRepository;
import com.urlshortener.service.exceptions.ShortUrlNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private HitCountingCache<String> cache;

    @Autowired
    private UrlMappingRepository urlMappingRepo;

    @Override
    public UrlMapping hitShortUrl(String shortUrl) {
        if (shortUrl.length() != SHORT_URL_LENGTH)
            throw new ShortUrlNotFoundException("Short URL should be of length " + SHORT_URL_LENGTH, shortUrl);

        Optional<UrlMapping> shortUrlOptional = urlMappingRepo.findByShortUrl(shortUrl);
        return shortUrlOptional.map(urlMapping -> {
            cache.hit(urlMapping.getTargetUrl());
            return urlMapping;
        }).orElseThrow(() -> new ShortUrlNotFoundException("Short URL is not registered earlier", shortUrl));
    }

}
