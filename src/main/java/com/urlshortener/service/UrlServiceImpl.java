package com.urlshortener.service;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.repo.UrlRepository;
import com.urlshortener.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private HitCountingCache<String> cache;

    @Autowired
    private UrlRepository urlRepo;

    @Override
    public UrlMapping hitShortUrl(String shortUrl) {
        if (shortUrl.length() != SHORT_URL_LENGTH)
            throw new NotFoundException(shortUrl);

        Optional<UrlMapping> shortUrlOptional = urlRepo.findByShortUrl(shortUrl);
        return shortUrlOptional.map(urlMapping -> {
            cache.hit(urlMapping.getTargetUrl());
            return urlMapping;
        }).orElseThrow(() -> new NotFoundException(shortUrl));
    }

}
