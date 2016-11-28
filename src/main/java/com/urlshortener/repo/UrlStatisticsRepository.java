package com.urlshortener.repo;

import com.urlshortener.model.UrlStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlStatisticsRepository extends JpaRepository<UrlStatistics, Integer> {

    Optional<UrlStatistics> findById(Integer id);

}
