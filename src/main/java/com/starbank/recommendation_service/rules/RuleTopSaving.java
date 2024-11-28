package com.starbank.recommendation_service.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RuleTopSaving implements RecommendationRuleSet {

    @Autowired
    RecommendationsRepository repository;

    @Override
    public Optional<RecommendationDTO> getRecommendation(String userId) {
        // Логика проверки, подходит ли пользователь под специальное предложение, будет зависеть от метода репозитория
        if (repository.checkRecommendtionTopSaving(userId) != null) {
            return Optional.of(new RecommendationDTO("59efc529-2fff-41af-baff-90ccd7402925",
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