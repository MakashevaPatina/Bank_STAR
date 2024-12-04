
package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class DynamicRecommendationService {

    private final DynamicRulesRepository dynamicRulesRepository;

    public DynamicRecommendationService(DynamicRulesRepository dynamicRulesRepository) {
        this.dynamicRulesRepository = dynamicRulesRepository;
    }


    /*public RecommendationResponse getRecommendations(String userId) {
        *//*List<RecommendationDTO> recommendations = dynamicRulesRepository.findAll().stream()
                .map(rule -> rule.getCondition(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());*//*
       // return new RecommendationResponse(recommendations, userId);
    }*/
}
