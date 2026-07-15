package com.example.Blink.resource.repository;

import com.example.Blink.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    @Query("SELECT r FROM Resource r JOIN FETCH r.user WHERE r.resourceId = :resourceId")
    Optional<Resource> findByIdWithUser(@Param("resourceId") UUID resourceId);

    @Modifying
    @Query("UPDATE Resource r SET r.clickCount = r.clickCount + 1 WHERE r.resourceId = :resourceId")
    void incrementClickCount(@Param("resourceId") UUID resourceId);
}