package com.example.Blink.url_click.repository;

import com.example.Blink.url_click.entity.UrlClick;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

    Page<UrlClick> findByUrl_UrlId(UUID urlId, Pageable pageable);

    Long countByUrl_UrlId(UUID urlId);

    Long countByUrl_UrlIdAndVisitedAtBetween(UUID urlId, Instant start, Instant end);

    @Query("SELECT uc.country FROM UrlClick uc WHERE uc.url.urlId = :urlId " +
           "GROUP BY uc.country ORDER BY COUNT(uc.country) DESC LIMIT 5")
    List<String> findTopCountriesByUrlId(@Param("urlId") UUID urlId);

    @Query("SELECT uc.browser FROM UrlClick uc WHERE uc.url.urlId = :urlId " +
           "GROUP BY uc.browser ORDER BY COUNT(uc.browser) DESC LIMIT 5")
    List<String> findTopBrowsersByUrlId(@Param("urlId") UUID urlId);


    @Transactional
    @Modifying
    void deleteByUrl_UrlId(UUID urlId);
}
