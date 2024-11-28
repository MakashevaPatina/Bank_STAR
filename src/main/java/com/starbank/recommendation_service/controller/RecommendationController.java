package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService, JdbcTemplate jdbcTemplate) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{user_id}")
    public RecommendationResponse getRecommendations(@PathVariable("user_id") String userId) {
        return recommendationService.getRecommendations(userId);
    }
}
