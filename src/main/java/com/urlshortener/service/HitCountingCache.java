package com.urlshortener.service;

import com.google.common.cache.Cache;

public class HitCountingCache {
    private final Cache cache;

    public HitCountingCache(Cache cache) {
        this.cache = cache;
    }

    //    public HitCountingCache(Cache cache) {
//        this.cache = CacheBuilder.newBuilder()
//                .maximumSize(10000)
//                .concurrencyLevel()
//                .expireAfterWrite(10, TimeUnit.MINUTES)
//                .removalListener(new RemovalListener<K1, V1>() {
//                    @Override
//                    public void onRemoval(RemovalNotification<K1, V1> notification) {
//                        //
//                    }
//                })
//                .build(new CacheLoader<Object, Object>() {
//                    @Override
//                    public Object load(Object key) throws Exception {
//
//                    }
//                });
//    }
}
