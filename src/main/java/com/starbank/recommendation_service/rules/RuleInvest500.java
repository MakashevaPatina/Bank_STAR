package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RuleInvest500 implements RecommendationRuleSet {

    @Autowired
    RecommendationsRepository repository;

    @Override
    public Optional<RecommendationDTO> getRecommendation(String userId) {
        if (repository.checkRecommendationInvest500(userId)) {
            return Optional.of(new RecommendationDTO("Invest500",
                    "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                            "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
                            "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                            "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. " +
                            "Откройте ИИС сегодня и станьте ближе к финансовой независимости!"));
        } else {
            return Optional.empty();
        }
    }
}