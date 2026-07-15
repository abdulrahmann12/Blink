package com.example.Blink.url_click.repository;

import com.example.Blink.url_click.entity.ResourceClick;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceClickRepository extends JpaRepository<ResourceClick, Long> {

    Page<ResourceClick> findByResource_ResourceId(UUID resourceId, Pageable pageable);

    @Cacheable(value = "totalClicks", key = "#resourceId")
    long countByResource_ResourceId(UUID resourceId);

    // ربطنا الكاش باليوم عشان لو استعلم عن يوم جديد ميتلخبطش
    @Cacheable(value = "clicksByDate", key = "#resourceId + '-' + #startDate.toEpochMilli()")
    long countByResource_ResourceIdAndVisitedAtBetween(UUID resourceId, Instant startDate, Instant endDate);

    @Cacheable(value = "topCountries", key = "#resourceId")
    @Query("SELECT r.country FROM ResourceClick r WHERE r.resource.resourceId = :resourceId GROUP BY r.country ORDER BY COUNT(r.country) DESC LIMIT 5")
    List<String> findTopCountriesByResource_ResourceId(@Param("resourceId") UUID resourceId);

    @Cacheable(value = "topBrowsers", key = "#resourceId")
    @Query("SELECT r.browser FROM ResourceClick r WHERE r.resource.resourceId = :resourceId GROUP BY r.browser ORDER BY COUNT(r.browser) DESC LIMIT 5")
    List<String> findTopBrowsersByResource_ResourceId(@Param("resourceId") UUID resourceId);
}