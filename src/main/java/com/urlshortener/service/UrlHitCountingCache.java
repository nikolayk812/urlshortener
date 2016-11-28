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
 * Emulation write-back cache for target URLs hit counting
 *
 * On first request we load counter from the DB or create a new one with 0 value
 * On removal from cache we flush value to the DB.
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
            Optional<UrlStatistics> urlStatistics = urlStatsRepo.findById(urlMapping.getId());
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
            Optional<UrlStatistics> urlStatisticsOptional = urlStatsRepo.findById(urlMapping.getId());
            int hitCounter = removalNotification.getValue().get();
            UrlStatistics urlStatistics = urlStatisticsOptional.map(us -> {
                        us.setHitCounter(hitCounter);
                        return us;
                    }
            ).orElseGet(() -> new UrlStatistics(hitCounter, urlMapping));
            urlStatsRepo.save(urlStatistics);
        } else {
            log.error("Url mapping for " + pair.getFirst() + " " + pair.getSecond() + " exists in cache, but not in DB");
        }
    }

}
