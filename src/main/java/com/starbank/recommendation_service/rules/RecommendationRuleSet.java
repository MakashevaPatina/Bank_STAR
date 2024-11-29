package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDTO;

import java.util.Optional;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> getRecommendation(String userId);
}
