package com.urlshortener.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Component
public class HitCountingCache {
    private final Cache<String, LongAdder> cache;

    @Autowired
    private AccountService service;

    public HitCountingCache() {
        this.cache = CacheBuilder.<String, LongAdder>newBuilder()
                .maximumSize(10000)
                //.concurrencyLevel()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<String, LongAdder>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, LongAdder> removalNotification) {
                        //TODO: service save
                    }
                }).build();
    }
}
