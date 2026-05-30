package com.example.Blink.url.repository;

import com.example.Blink.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByCustomAlias(String customAlias);

    boolean existsByShortUrl(String shortUrl);

    boolean existsByCustomAlias(String customAlias);

    @Modifying
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1 WHERE u.urlId = :id")
    void incrementClickCount(@Param("id") UUID id);
}
