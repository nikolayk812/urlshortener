package com.urlshortener.model;

import javax.persistence.*;

/**
 * URL statistics (redirects)
 *
 * Separated intentionally from {@link UrlMapping}
 */
@Entity
@Table(name = "url_stats")
public class UrlStatistics extends BaseEntity {

    @Column(name = "hit_counter")
    private int hitCounter;

    @OneToOne
    @JoinColumn(name = "url_id", unique = true, nullable = false)
    private UrlMapping urlMapping;

    public UrlStatistics() {
    }

    public UrlStatistics(int hitCounter, UrlMapping urlMapping) {
        this.hitCounter = hitCounter;
        this.urlMapping = urlMapping;
    }

    public int getHitCounter() {
        return hitCounter;
    }

    public void setHitCounter(int hitCounter) {
        this.hitCounter = hitCounter;
    }

    public UrlMapping getUrlMapping() {
        return urlMapping;
    }

}
