package com.urlshortener.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "stats")
public class UrlStatistics {

    @Column(name = "hit_counter")
    private int hitCounter;

    //TODO: foreign key?
    @OneToOne
    @JoinColumn(name = "url_id", unique = true, nullable = false)
    private UrlMapping urlMapping;

    public UrlStatistics() {
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

    public void setUrlMapping(UrlMapping urlMapping) {
        this.urlMapping = urlMapping;
    }
}
