package com.example.Blink.url.repository;

import com.example.Blink.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByCustomAlias(String customAlias);

    boolean existsByShortUrl(String shortUrl);

    boolean existsByCustomAlias(String customAlias);
}
