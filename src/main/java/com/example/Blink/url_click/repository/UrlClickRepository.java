package com.example.Blink.url_click.repository;

import com.example.Blink.url_click.entity.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

    List<UrlClick> findByUrl_UrlId(UUID urlId);
}
