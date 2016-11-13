package com.urlshortener.repo;

import com.urlshortener.model.UrlStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlStatisticsRepository extends JpaRepository<UrlStatistics, Integer> {
}
