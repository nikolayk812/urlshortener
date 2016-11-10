package com.urlshortener.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

public class StatisticsResponse {
    private Map<String, Integer> urlStats;

    public StatisticsResponse() {
    }

    public StatisticsResponse(Map<String, Integer> urlStats) {
        this.urlStats = urlStats;
    }

    @JsonAnyGetter
    public Map<String, Integer> getUrlStats() {
        return urlStats;
    }

    public void setUrlStats(Map<String, Integer> urlStats) {
        this.urlStats = urlStats;
    }
}
