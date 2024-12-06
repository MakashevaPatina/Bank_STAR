package com.starbank.recommendation_service.repository;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<CacheKey, Integer> transactionTypeProductTypeCache;
    private final Cache<CacheKey, Integer> transactionProductTypeExcludedCache;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.transactionTypeProductTypeCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();

        this.transactionProductTypeExcludedCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
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
        CacheKey cacheKey = new CacheKey(user, productType, transactionType);
        Integer result = transactionTypeProductTypeCache.getIfPresent(cacheKey);
        if (result != null) {
            return result;
        }
        try {
            result = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(amount), 0) FROM products p " +
                            "JOIN transactions t ON t.product_id = p.id " +
                            "WHERE user_id = ? AND p.type = ? AND t.type = ?",
                    Integer.class, user, productType, transactionType);

            if (result != null) {
                transactionTypeProductTypeCache.put(cacheKey, result);
            } else {
                transactionTypeProductTypeCache.put(cacheKey, 0);
            }
            return result != null ? result : 0;
        } catch (
                Exception e) {
            return 0;
        }
    }

    public int getAmountOfTransactionsForUserProductTypeExcluded(UUID user, String productType) {
        CacheKey cacheKey = new CacheKey(user, productType, null);
        Integer result = transactionProductTypeExcludedCache.getIfPresent(cacheKey);
        if (result != null) {
            return result;
        }
        try {
            result = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(amount), 0) FROM products p " +
                            "JOIN transactions t ON t.product_id = p.id " +
                            "WHERE user_id = ? AND p.type != ?",
                    Integer.class, user, productType);
            if (result != null) {
                transactionProductTypeExcludedCache.put(cacheKey, result);
            } else {
                transactionProductTypeExcludedCache.put(cacheKey, 0); // Если null, кешируем 0
            }

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

    private static class CacheKey {
        private final UUID user;
        private final String productType;
        private final String transactionType;

        public CacheKey(UUID user, String productType, String transactionType) {
            this.user = user;
            this.productType = productType;
            this.transactionType = transactionType;
        }

        public UUID getUser() {
            return user;
        }

        public String getProductType() {
            return productType;
        }

        public String getTransactionType() {
            return transactionType;
        }
    }
}
