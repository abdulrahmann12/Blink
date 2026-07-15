package com.example.Blink.url.repository;

import com.example.Blink.url.dto.UrlDashboardProjection;
import com.example.Blink.url.entity.Url;
import com.example.Blink.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
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

    @Query(
            value = "SELECT u FROM Url u JOIN FETCH u.user WHERE u.user.userId = :userId",
            countQuery = "SELECT COUNT(u) FROM Url u WHERE u.user.userId = :userId"
    )
    Page<Url> findAllByUser_UserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
       SELECT u
       FROM Url u
       JOIN FETCH u.user
       WHERE u.urlId = :id
       AND u.user.userId = :userId
       """)
    Optional<Url> findByIdAndUser_UserId(
            @Param("id") UUID id,
            @Param("userId") Long userId
    );
    long countByUser(User user);

    long countByUserAndActiveTrue(User user);

    @Query("SELECT COALESCE(SUM(u.clickCount),0) FROM Url u WHERE u.user = :user")
    long sumClicksByUser(User user);

    @Query("""
    SELECT COUNT(u)
    FROM Url u
    WHERE u.user = :user
    AND u.expireAt < :now
""")
    long countExpiredUrls(User user, Instant now);

    @Query("""
    SELECT u
    FROM Url u
    JOIN FETCH u.user
    WHERE u.urlId = :id
""")
    Optional<Url> findByIdWithUser(@Param("id") UUID id);

    @Query("SELECT u FROM Url u WHERE u.expireAt IS NOT NULL AND u.expireAt < :now")
    List<Url> findByExpireAtBefore(@Param("now") Instant now);

    @Query("""
    SELECT u
    FROM Url u
    JOIN FETCH u.user usr
    WHERE
        LOWER(COALESCE(u.title, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.originalUrl) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.shortUrl) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(COALESCE(u.customAlias, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(usr.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Url> searchUrls(@Param("keyword") String keyword, Pageable pageable);

    Page<Url> findByActiveFalse(Pageable pageable);

    @Query("""
SELECT u
FROM Url u
WHERE u.expireAt IS NOT NULL
AND u.expireAt < :now
""")
    Page<Url> findExpiredUrls(
            @Param("now") Instant now,
            Pageable pageable
    );

    long countByActiveTrue();

    long countByActiveFalse();

    @Query("SELECT COALESCE(SUM(u.clickCount),0) FROM Url u")
    long sumClicks();

    @Query("""
SELECT COUNT(u)
FROM Url u
WHERE u.expireAt IS NOT NULL
AND u.expireAt < :now
""")
    long countExpiredUrls(@Param("now") Instant now);

    Page<Url> findAllByOrderByClickCountDesc(Pageable pageable);

    @Query("""
SELECT
    COUNT(u) AS totalUrls,
    SUM(CASE WHEN u.active = true THEN 1 ELSE 0 END) AS activeUrls,
    SUM(CASE WHEN u.active = false THEN 1 ELSE 0 END) AS inactiveUrls,
    SUM(CASE
            WHEN u.expireAt IS NOT NULL
             AND u.expireAt < :now
            THEN 1 ELSE 0
        END) AS expiredUrls,
    COALESCE(SUM(u.clickCount), 0) AS totalClicks
FROM Url u
""")
    UrlDashboardProjection getDashboardStatistics(@Param("now") Instant now);

    boolean existsByResource_ResourceId(UUID resourceId);
}
