package com.starbank.recommendation_service.rules;

import java.util.Optional;

public interface RecommendationRuleSet {
    Optional<Recommendation> getRecommendation(Long userId);
}
