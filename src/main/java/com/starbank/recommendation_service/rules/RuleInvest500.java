package com.starbank.recommendation_service.rules;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RuleInvest500 implements RecommendationRuleSet {

    Repository repository;

    @Override
    public Optional<Recommendation> getRecommendation(Long userId) {
        // Логика проверки, подходит ли пользователь под специальное предложение, будет зависеть от метода репозитория
        if (repository.checkRecommendtionInvest500(userId) != null) {
            return Optional.of(new Recommendation("147f6a0f-3b91-413b-ab99-87f081d60d5a",
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