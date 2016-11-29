package com.urlshortener.model;

import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * URL statistics (redirects)
 *
 * Separated intentionally from {@link UrlMapping}
 */
@Entity
@Table(name = "url_stats")
@Access(value = AccessType.FIELD)
public class UrlStatistics {

    @Id
    @Column(name = "url_id")
    private Integer id;

    @Min(value = 0)
    @Column(name = "hit_counter")
    private int hitCounter;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "url_id")
    private UrlMapping urlMapping;

    public UrlStatistics() {
    }

    public UrlStatistics(int hitCounter, UrlMapping urlMapping) {
        this.hitCounter = hitCounter;
        this.urlMapping = urlMapping;
        this.id = urlMapping.getId();
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

    public Integer getId() {
        return id;
    }

}
