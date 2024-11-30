package com.starbank.recommendation_service.repository;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID user) {
        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                    Integer.class,
                    user);
            return result != null ? result : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getAmountOfTransactionTypeForProductTypeForUser(UUID user, String productType, String transactionType) {
        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(amount), 0) FROM products p " +
                            "JOIN transactions t ON t.product_id = p.id " +
                            "WHERE user_id = ? AND p.type = ? AND t.type = ?",
                    Integer.class, user, productType, transactionType);
            return result != null ? result : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getAmountOfTransactionsForUserProductTypeExcluded(UUID user, String productType) {
        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(amount), 0) FROM products p " +
                            "JOIN transactions t ON t.product_id = p.id " +
                            "WHERE user_id = ? AND p.type != ?",
                    Integer.class, user, productType);
            return result != null ? result : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean checkRecommendationInvest500(String id) {

        UUID uuid = UUID.fromString(id);

        if (getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") +
                getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 0 &&

                getAmountOfTransactionsForUserProductTypeExcluded(uuid, "INVEST") > 0 &&

                getAmountOfTransactionTypeForProductTypeForUser(uuid, "SAVING", "DEPOSIT") > 1000) {

            return true;
        }
        return false;
    }

    public boolean checkRecommendationSimpleCredit(String id) {

        UUID uuid = UUID.fromString(id);

        if (getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") +
                getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 0 &&

                (getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >= 50_000 ||
                        getAmountOfTransactionTypeForProductTypeForUser(uuid, "SAVING", "DEPOSIT") >= 50_000) &&

                getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >
                        getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW")) {

            return true;
        }
        return false;
    }

    public boolean checkRecommendationTopSaving(String id) {

        UUID uuid = UUID.fromString(id);

        if (getAmountOfTransactionsForUserProductTypeExcluded(uuid, "CREDIT") > 0 &&

                getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "DEPOSIT") >
                        getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") &&

                getAmountOfTransactionTypeForProductTypeForUser(uuid, "DEBIT", "WITHDRAW") > 100_000) {

            return true;
        }
        return false;
    }
}
