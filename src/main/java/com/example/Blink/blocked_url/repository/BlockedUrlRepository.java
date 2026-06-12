package com.example.Blink.blocked_url.repository;

import com.example.Blink.blocked_url.entity.BlockedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedUrlRepository extends JpaRepository<BlockedUrl, Long> {
    boolean existsByDomain(String domain);

    Optional<BlockedUrl> findByDomain(String domain);
}
