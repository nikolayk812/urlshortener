package com.urlshortener.model;

import javax.persistence.*;

@Entity
@Table(name = "stats")
public class UrlStatistics {

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
