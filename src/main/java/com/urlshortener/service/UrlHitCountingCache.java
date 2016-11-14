package com.urlshortener.service;

import com.google.common.cache.RemovalNotification;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.model.UrlStatistics;
import com.urlshortener.repo.UrlRepository;
import com.urlshortener.repo.UrlStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlHitCountingCache extends HitCountingCache<String>{

    @Autowired
    private UrlRepository urlRepo;

    @Autowired
    private UrlStatisticsRepository urlStatsRepo;

    @Override
    protected AtomicInteger loadImpl(String key) throws Exception {
        Optional<UrlMapping> urlMappingOptional = urlRepo.findByTargetUrlAndRedirectType(key, RedirectType.FOUND);
        return urlMappingOptional.map(urlMapping -> {
            Optional<UrlStatistics> urlStatistics = urlStatsRepo.findByUrlMapping(urlMapping);
            if (urlStatistics.isPresent()) {
                return new AtomicInteger(urlStatistics.get().getHitCounter());
            } else {
                return new AtomicInteger(0);
            }
        }).orElseGet(() -> new AtomicInteger(0));
    }

    @Override
    protected void onRemovalImpl(RemovalNotification<String, AtomicInteger> removalNotification) {
        Optional<UrlMapping> urlMapping = urlRepo.findByTargetUrlAndRedirectType(removalNotification.getKey(), RedirectType.FOUND);
        UrlStatistics urlStatistics = new UrlStatistics(removalNotification.getValue().get(), urlMapping.get());
        urlStatsRepo.save(urlStatistics);
    }

}
