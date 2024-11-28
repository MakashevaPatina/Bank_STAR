package com.starbank.recommendation_service.rules;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> getRecommendation(String userId);
}
