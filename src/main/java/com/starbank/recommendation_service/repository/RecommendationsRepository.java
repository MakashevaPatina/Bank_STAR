package com.starbank.recommendation_service.repository;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<CacheKey, Integer> transactionTypeProductTypeCache;
    private final Cache<CacheKey, Integer> transactionProductTypeExcludedCache;
    private final Cache<CacheKey, Integer> transactionCountForProductTypeCache;
    private final Cache<CacheKey, Integer> activeTransactionCountCache;
    private final Cache<CacheKey, Integer> transactionSumCache;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.transactionTypeProductTypeCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();

        this.transactionProductTypeExcludedCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();

        this.transactionCountForProductTypeCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();

        this.activeTransactionCountCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
        this.transactionSumCache = Caffeine.newBuilder()
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

            transactionTypeProductTypeCache.put(cacheKey, result != null ? result : 0);
            return result != null ? result : 0;
        } catch (Exception e) {
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
            transactionProductTypeExcludedCache.put(cacheKey, result != null ? result : 0);
            return result != null ? result : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTransactionCountForProductType(String userId, String productType) {
        CacheKey cacheKey = new CacheKey(UUID.fromString(userId), productType, null);
        Integer cachedResult = transactionCountForProductTypeCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        try {
            String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND product_id = (SELECT id FROM products WHERE type = ? LIMIT 1)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
            int result = count != null ? count : 0;
            transactionCountForProductTypeCache.put(cacheKey, result);
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getActiveTransactionCountForProductType(String userId, String productType) {
        CacheKey cacheKey = new CacheKey(UUID.fromString(userId), productType, "ACTIVE");
        Integer cachedResult = activeTransactionCountCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        try {
            String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND product_id = (SELECT id FROM products WHERE type = ? LIMIT 1) AND type IN ('DEPOSIT', 'WITHDRAW')";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
            int result = count != null ? count : 0;
            activeTransactionCountCache.put(cacheKey, result);
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTransactionSum(String userId, String productType, String transactionType) {
        CacheKey cacheKey = new CacheKey(UUID.fromString(userId), productType, transactionType);
        Integer cachedResult = transactionSumCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        try {
            String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND product_id = (SELECT id FROM products WHERE type = ? LIMIT 1) AND type = ?";
            Integer sum = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType, transactionType);
            int result = sum != null ? sum : 0;
            transactionSumCache.put(cacheKey, result);
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public void clearAllCaches() {
        transactionTypeProductTypeCache.invalidateAll();
        transactionProductTypeExcludedCache.invalidateAll();
        transactionCountForProductTypeCache.invalidateAll();
        activeTransactionCountCache.invalidateAll();
        transactionSumCache.invalidateAll();
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
