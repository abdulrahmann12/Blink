package com.example.Blink.url.service;

import com.example.Blink.exception.*;
import com.example.Blink.url.dto.*;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.mapper.UrlMapper;
import com.example.Blink.url.repository.UrlRepository;
import com.example.Blink.user.dto.UserDashboardProjection;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Validated
public class AdminUrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UserRepository userRepository;

    @Transactional
    public Page<UrlResponse> getAllUrls(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Url> urls = urlRepository.findAll(pageable);
        return urls.map(urlMapper::toResponse);
    }

    @Transactional
    public Page<UrlResponse> searchUrls(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return urlRepository.searchUrls(keyword, pageable)
                .map(urlMapper::toResponse);
    }

    @Transactional
    public Page<UrlResponse> getInactiveUrls(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return urlRepository.findByActiveFalse(pageable)
                .map(urlMapper::toResponse);
    }


    @Transactional
    public Page<UrlResponse> getExpiredUrls(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return urlRepository
                .findExpiredUrls(Instant.now(), pageable)
                .map(urlMapper::toResponse);
    }

    public AdminDashboardResponse getDashboard() {

        UrlDashboardProjection urlStats =
                urlRepository.getDashboardStatistics(Instant.now());

        UserDashboardProjection userStats =
                userRepository.getDashboardStatistics();

        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalUsers(userStats.getTotalUsers());
        response.setTotalActiveUsers(userStats.getActiveUsers());
        response.setTotalDeActiveUsers(userStats.getInactiveUsers());
        response.setTotalNOtVerifiedUsers(userStats.getNotVerifiedUsers());

        response.setTotalUrls(urlStats.getTotalUrls());
        response.setActiveUrls(urlStats.getActiveUrls());
        response.setInactiveUrls(urlStats.getInactiveUrls());
        response.setExpiredUrls(urlStats.getExpiredUrls());
        response.setTotalClicks(urlStats.getTotalClicks());

        return response;
    }

    @Transactional
    public Page<UrlResponse> getTopUrls(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return urlRepository
                .findAllByOrderByClickCountDesc(pageable)
                .map(urlMapper::toResponse);
    }

    public Page<UrlResponse> getUserUrls(long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return urlRepository.findAllByUser_UserId(user.getUserId(), pageable)
                .map(urlMapper::toResponse);
    }
}
