package com.urlshortener.service;

import com.google.common.cache.RemovalNotification;
import com.urlshortener.model.RedirectType;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.model.UrlStatistics;
import com.urlshortener.repo.UrlMappingRepository;
import com.urlshortener.repo.UrlStatisticsRepository;
import com.urlshortener.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cache for target URLs
 */
public class UrlHitCountingCache extends HitCountingCache<Pair<String, RedirectType>> {
    private final static Logger log = LoggerFactory.getLogger(UrlHitCountingCache.class);

    @Autowired
    private UrlMappingRepository urlMappingRepo;

    @Autowired
    private UrlStatisticsRepository urlStatsRepo;

    public UrlHitCountingCache(int maxSize, long expireDurationMs) {
        super(maxSize, expireDurationMs);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    protected AtomicInteger loadImpl(Pair<String, RedirectType> key) {
        Optional<UrlMapping> urlMappingOptional = urlMappingRepo.findByTargetUrlAndRedirectType(key.getFirst(), key.getSecond());
        return urlMappingOptional.map(urlMapping -> {
            Optional<UrlStatistics> urlStatistics = urlStatsRepo.findByUrlMapping(urlMapping);
            if (urlStatistics.isPresent()) {
                return new AtomicInteger(urlStatistics.get().getHitCounter());
            } else {
                return new AtomicInteger(0);
            }
        }).orElseGet(() -> new AtomicInteger(0));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    protected void onRemovalImpl(RemovalNotification<Pair<String, RedirectType>, AtomicInteger> removalNotification) {
        Pair<String, RedirectType> pair = removalNotification.getKey();
        Optional<UrlMapping> urlMappingOptional = urlMappingRepo.findByTargetUrlAndRedirectType(pair.getFirst(), pair.getSecond());
        if (urlMappingOptional.isPresent()) {
            UrlMapping urlMapping = urlMappingOptional.get();
            Optional<UrlStatistics> urlStatisticsOptional = urlStatsRepo.findByUrlMapping(urlMapping);
            int hitCounter = removalNotification.getValue().get();
            UrlStatistics urlStatistics = urlStatisticsOptional.map(us -> {
                        us.setHitCounter(hitCounter);
                        return us;
                    }
            ).orElseGet(() -> new UrlStatistics(hitCounter, urlMapping));
            urlStatsRepo.save(urlStatistics);
        } else {
            log.error("");
        }
    }

}
