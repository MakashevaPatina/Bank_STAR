package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RecommendationChecker {

    private final RecommendationsRepository recommendationsRepository;

    @Autowired
    public RecommendationChecker(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    public boolean checkRecommendationInvest500(String id) {
        UUID uuid = UUID.fromString(id);


        if (recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") +
                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 0 &&


                recommendationsRepository.getAmountOfTransactionsForUserProductTypeExcluded(uuid, "INVEST") > 0 &&


                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "SAVING", "DEPOSIT") > 1000) {

            return true;
        }
        return false;
    }

    public boolean checkRecommendationTopSaving(String id) {
        UUID uuid = UUID.fromString(id);

        if (recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "CREDIT", "DEBIT") == 0 &&


                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >
                        recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") &&


                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 100_000) {

            return true;
        }
        return false;
    }

    public boolean checkRecommendationSimpleCredit(String id) {
        UUID uuid = UUID.fromString(id);

        if (recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") +
                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 0 &&

                (recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >= 50_000 ||
                        recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "SAVING", "DEPOSIT") >= 50_000) &&

                recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >
                        recommendationsRepository.getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW")) {

            return true;
        }
        return false;
    }
}
