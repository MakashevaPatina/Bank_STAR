package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import com.starbank.recommendation_service.rules.dynamic.Condition;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;
    private final DynamicRulesRepository dynamicRulesRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationService(List<RecommendationRuleSet> ruleSets, DynamicRulesRepository dynamicRulesRepository,
                                 RecommendationsRepository recommendationsRepository, JdbcTemplate jdbcTemplate) {
        this.ruleSets = ruleSets;
        this.dynamicRulesRepository = dynamicRulesRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public RecommendationResponse getRecommendations(String userId) {
        List<DynamicRule> dynamicRules = dynamicRulesRepository.findAll();
        List<RecommendationDTO> recommendations = ruleSets.stream()
                .map(rule -> rule.getRecommendation(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        dynamicRules.stream()
                .filter(rule -> applyDynamicRule(rule, userId))  // Применяем правило
                .map(rule -> new RecommendationDTO(rule.getProductName(), rule.getId().toString(), rule.getProductText()))  // Преобразуем динамическое правило в рекомендацию
                .forEach(recommendations::add);

        return new RecommendationResponse(recommendations, userId);

    }

    private boolean applyDynamicRule(DynamicRule rule, String userId) {

        for (Condition condition : rule.getConditions()) {
            boolean conditionMet = processQuery(userId, condition);
            if (!conditionMet) {
                return false;
            }
        }
        return true;
    }

    public boolean processQuery(String userId, Condition condition) {
        String queryType = condition.getQuery();
        List<String> arguments = condition.getArguments();
        boolean negate = condition.isNegate();

        switch (queryType) {
            case "USER_OF":
                return handleUserOfQuery(userId, arguments, negate);
            case "ACTIVE_USER_OF":
                return handleActiveUserOfQuery(userId, arguments, negate);
            case "TRANSACTION_SUM_COMPARE":
                return handleTransactionSumCompareQuery(userId, arguments, negate);
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                return handleTransactionSumCompareDepositWithdrawQuery(userId, arguments, negate);
            default:
                throw new IllegalArgumentException("Unknown query type: " + queryType);
        }
    }

    private boolean handleUserOfQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();

        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND product_type = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);

        boolean isUser = count != null && count > 0;

        return negate ? !isUser : isUser;
    }

    private boolean handleActiveUserOfQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();

        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type IN ('DEPOSIT', 'WITHDRAW')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);

        boolean isActiveUser = count != null && count >= 5;

        return negate ? !isActiveUser : isActiveUser;
    }

    private boolean handleTransactionSumCompareQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        String transactionType = arguments.get(1).toUpperCase();
        String comparisonOperator = arguments.get(2);
        int threshold = Integer.parseInt(arguments.get(3));

        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = ?";
        Integer totalTransactionSum = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType, transactionType);

        boolean result = compareSum(totalTransactionSum != null ? totalTransactionSum : 0, threshold, comparisonOperator);

        return negate ? !result : result;
    }


    private boolean handleTransactionSumCompareDepositWithdrawQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        String comparisonOperator = arguments.get(1);


        String depositSql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = 'DEPOSIT'";
        Integer depositSum = jdbcTemplate.queryForObject(depositSql, Integer.class, userId, productType);

        String withdrawSql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = 'WITHDRAW'";
        Integer withdrawSum = jdbcTemplate.queryForObject(withdrawSql, Integer.class, userId, productType);

        boolean result = compareSum(depositSum != null ? depositSum : 0, withdrawSum != null ? withdrawSum : 0, comparisonOperator);

        return negate ? !result : result;
    }

    private boolean compareSum(int sum1, int sum2, String comparisonOperator) {
        switch (comparisonOperator) {
            case ">":
                return sum1 > sum2;
            case "<":
                return sum1 < sum2;
            case "=":
                return sum1 == sum2;
            case ">=":
                return sum1 >= sum2;
            case "<=":
                return sum1 <= sum2;
            default:
                throw new IllegalArgumentException("Invalid comparison operator: " + comparisonOperator);
        }
    }
}