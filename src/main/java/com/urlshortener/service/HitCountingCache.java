package com.urlshortener.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: Tx Context?
public abstract class HitCountingCache<T> {
    private final LoadingCache<T, AtomicInteger> cache;

    HitCountingCache() {
        //TODO: move values to properties
        this.cache = CacheBuilder.<T, AtomicInteger>newBuilder()
                .maximumSize(10000)
                //.concurrencyLevel()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(this::onRemovalImpl)
                .build(new CacheLoader<T, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(T key) throws Exception {
                        return loadImpl(key);
                    }
                });
    }

    public int getCount(T key) {
        try {
            AtomicInteger counter = cache.get(key);
            return counter.get();
        } catch (ExecutionException e) {
            //TODO;
            return 0;
        }
    }

    public void hit(T key) {
        try {
            cache.get(key).incrementAndGet();
        } catch (ExecutionException e) {
            //TODO: log
        }
    }

    protected abstract AtomicInteger loadImpl(T key) throws Exception;

    protected abstract void onRemovalImpl(RemovalNotification<T, AtomicInteger> removalNotification);

}
