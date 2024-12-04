
package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class DynamicRecommendationService {

    private final DynamicRulesRepository dynamicRulesRepository;

    public DynamicRecommendationService(DynamicRulesRepository dynamicRulesRepository) {
        this.dynamicRulesRepository = dynamicRulesRepository;
    }


    /*public Optional<RecommendationDTO> getRecommendation(String userID) {
        RecommendationDTO recommendationDTO;
        recommendationDTO = null;
        List<DynamicRule> rules = dynamicRulesRepository.findAll();
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i) = true) { //Тут должна быть логика в условии
                recommendationDTO = new RecommendationDTO(rules.get(i).getProductName(),
                        rules.get(i).getId().toString(),
                        rules.get(i).getProductText());
            }
        }
        if (recommendationDTO != null) {
            return Optional.of(recommendationDTO);
        } else {
            return Optional.empty();
        }
    }*/
}
