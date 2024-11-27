package com.starbank.recommendation_service.rules;

public interface RecommendationRuleSet {
    Optional<Recommendation> getRecommendation(Long userId);
}
