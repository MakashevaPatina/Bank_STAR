package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    private final RecommendationsRepository recommendationsRepository;

    @Autowired
    public CacheController(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @PostMapping("/management/clear-caches")
    public void clearCaches() {
        recommendationsRepository.clearAllCaches();
    }

}
