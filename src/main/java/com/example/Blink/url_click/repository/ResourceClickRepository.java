package com.example.Blink.url_click.repository;

import com.example.Blink.url_click.entity.ResourceClick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceClickRepository extends JpaRepository<ResourceClick, Long> {

    Page<ResourceClick> findByResource_ResourceId(UUID resourceId, Pageable pageable);

    long countByResource_ResourceId(UUID resourceId);

    long countByResource_ResourceIdAndVisitedAtBetween(UUID resourceId, Instant startDate, Instant endDate);

    @org.springframework.data.jpa.repository.Query("SELECT r.country FROM ResourceClick r WHERE r.resource.resourceId = :resourceId GROUP BY r.country ORDER BY COUNT(r.country) DESC LIMIT 5")
    List<String> findTopCountriesByResource_ResourceId(@org.springframework.data.repository.query.Param("resourceId") UUID resourceId);

    @org.springframework.data.jpa.repository.Query("SELECT r.browser FROM ResourceClick r WHERE r.resource.resourceId = :resourceId GROUP BY r.browser ORDER BY COUNT(r.browser) DESC LIMIT 5")
    List<String> findTopBrowsersByResource_ResourceId(@org.springframework.data.repository.query.Param("resourceId") UUID resourceId);
}