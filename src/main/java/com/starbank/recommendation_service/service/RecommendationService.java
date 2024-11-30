package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }


    public RecommendationResponse getRecommendations(String userId) {

        List<RecommendationDTO> recommendations = ruleSets.stream()
                .map(rule -> rule.getRecommendation(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return new RecommendationResponse(recommendations, userId);
    }
}
