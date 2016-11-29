package com.urlshortener.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

public abstract class HitCountingCache<T> {
    private final static Logger log = LoggerFactory.getLogger(HitCountingCache.class);
    private final LoadingCache<T, AtomicInteger> cache;

    /**
     * Constructs cache
     *
     * @param maxSize max size of cache
     * @param expireDurationMs expiration duration in milliseconds
     */
    HitCountingCache(int maxSize, long expireDurationMs) {
        this.cache = CacheBuilder.<T, AtomicInteger>newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireDurationMs, TimeUnit.MILLISECONDS)
                .removalListener(this::onRemovalImpl)
                .build(new CacheLoader<T, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(T key) throws Exception {
                        return loadImpl(key);
                    }
                });
    }

    public int getCounter(T key) {
        requireNonNull(key);
        try {
            int counter = cache.get(key).get();
            log.debug("For key {} counter {}", key, counter);
            return counter;
        } catch (ExecutionException e) {
            log.error("Failed to get counter for key " + key + ", returning 0 as default value", e);
            return 0;
        }
    }

    public void hit(T key) {
        requireNonNull(key);
        try {
            int counter = cache.get(key).incrementAndGet();
            log.debug("Hit key {} counter: {}", key, counter);
        } catch (ExecutionException e) {
            log.error("Failed to increment counter for key " + key + ", skipping", e);
        }
    }

    protected abstract AtomicInteger loadImpl(T key);

    protected abstract void onRemovalImpl(RemovalNotification<T, AtomicInteger> removalNotification);

}
