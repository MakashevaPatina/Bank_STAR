package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RuleTopSaving implements RecommendationRuleSet {

    private final RecommendationChecker recommendationChecker;

    public RuleTopSaving(RecommendationChecker recommendationChecker) {
        this.recommendationChecker = recommendationChecker;
    }


    @Override
    public Optional<RecommendationDTO> getRecommendation(String userId) {
        if (recommendationChecker.checkRecommendationSimpleCredit(userId)) {
            return Optional.of(new RecommendationDTO("TopSaving",
                    "59efc529-2fff-41af-baff-90ccd7402925",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент," +
                            " который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!" +
                            "Преимущества «Копилки»:" +
                            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет." +
                            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости." +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг." +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"));
        } else {
            return Optional.empty();
        }
    }
}