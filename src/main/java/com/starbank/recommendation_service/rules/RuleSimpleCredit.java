package com.starbank.recommendation_service.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RuleSimpleCredit implements RecommendationRuleSet {

    @Autowired
    RecommendationsRepository repository;

    @Override
    public Optional<RecommendationDTO> getRecommendation(String userId) {
        // Логика проверки, подходит ли пользователь под специальное предложение, будет зависеть от метода репозитория
        if (repository.checkRecommendtionSimpleCredit(userId) != null) {
            return Optional.of(new RecommendationDTO("ab138afb-f3ba-4a93-b74f-0fcee86d447f",
                    "Откройте мир выгодных кредитов с нами!" +
                            "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! " +
                            "Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту." +
                            "Почему выбирают нас:" +
                            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов." +
                            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении." +
                            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое." +
                            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"));
        } else {
            return Optional.empty();
        }
    }
}