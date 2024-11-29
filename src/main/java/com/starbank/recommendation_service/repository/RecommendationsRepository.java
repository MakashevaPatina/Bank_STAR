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
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                user);
        return result != null ? result : 0;
    }

    public int checkDBForProductTypePlusTransactionType(UUID user, String productType, String transactionType) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM products p JOIN transactions t ON t.product_id = p.id WHERE user_id = ? AND " +
                        "p.type = ? AND t.type = ?",
                Integer.class, user, productType, transactionType);
        return result != null ? result : 0;
    }

    public int checkDBProductTypeExcluded(UUID user, String productType) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM products p JOIN transactions t ON t.product_id = p.id WHERE user_id = ? AND " +
                        "p.type != ?",
                Integer.class, user, productType);
        return result != null ? result : 0;
    }

    public boolean checkRecommendationInvest500(String id) {

        UUID uuid = UUID.fromString(id);

        if (checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "DEPOSIT") +
                checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "WITHDRAW") > 0 &&

                checkDBProductTypeExcluded(uuid, "INVEST") > 0 &&

                checkDBForProductTypePlusTransactionType(uuid, "SAVING", "DEPOSIT") > 1000) {

           return true;
        }
        return false;
    }

    public boolean checkRecommendationSimpleCredit(String id) {

        UUID uuid = UUID.fromString(id);

        if (checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "DEPOSIT") +
                checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "WITHDRAW") > 0 &&

                (checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "DEPOSIT") >= 50_000 ||
                        checkDBForProductTypePlusTransactionType(uuid, "SAVING", "DEPOSIT") >= 50_000) &&

                checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "DEPOSIT") >
                        checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "WITHDRAW")) {

            return true;
        }
        return false;
    }
    public boolean checkRecommendationTopSaving(String id) {

        UUID uuid = UUID.fromString(id);

        if (checkDBProductTypeExcluded(uuid, "CREDIT") > 0 &&

                checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "DEPOSIT") >
                        checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "WITHDRAW") &&

                checkDBForProductTypePlusTransactionType(uuid, "DEBIT", "WITHDRAW") > 100_000) {

            return true;
        }
        return false;
    }
}
