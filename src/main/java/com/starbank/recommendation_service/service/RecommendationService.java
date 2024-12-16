package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import com.starbank.recommendation_service.rules.dynamic.Condition;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;
    private final DynamicRulesRepository dynamicRulesRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RuleStatService ruleStatService;
    private final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    public RecommendationService(List<RecommendationRuleSet> ruleSets, DynamicRulesRepository dynamicRulesRepository, RecommendationsRepository recommendationsRepository, JdbcTemplate jdbcTemplate, RuleStatService ruleStatService) {
        this.ruleSets = ruleSets;
        this.dynamicRulesRepository = dynamicRulesRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.ruleStatService = ruleStatService;
    }


    public RecommendationResponse getRecommendations(String userId) {
        List<DynamicRule> dynamicRules = dynamicRulesRepository.findAll();
        List<RecommendationDTO> recommendations = ruleSets.stream()
                .map(rule -> rule.getRecommendation(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        dynamicRules.stream()
                .filter(rule -> applyDynamicRule(rule, userId))
                .map(rule -> new RecommendationDTO(rule.getProductName(), rule.getId().toString(), rule.getProductText()))
                .forEach(recommendations::add);

        return new RecommendationResponse(recommendations, userId);

    }

    private boolean applyDynamicRule(DynamicRule rule, String userId) {
        if (rule == null) {
            logger.warn("Dynamic rule is null for user: {}", userId);
            return false;
        }
        for (Condition condition : rule.getConditions()) {
            boolean conditionMet = processQuery(userId, condition);
            if (!conditionMet) {
                return false;
            }
        }
        ruleStatService.incrementRuleStat(rule.getId().toString());
        return true;
    }

    public boolean processQuery(String userId, Condition condition) {
        String queryType = condition.getQuery();
        List<String> arguments = condition.getArguments();
        boolean negate = condition.isNegate();
        logger.debug("Processing query: {} with arguments: {}", queryType, arguments);
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
                logger.error("Unknown query type: {}", queryType);
                throw new IllegalArgumentException("Unknown query type: " + queryType);
        }
    }

    private boolean handleUserOfQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        int count = recommendationsRepository.getTransactionCountForProductType(userId, productType);
        boolean isUser = count > 0;
        return negate ? !isUser : isUser;
    }

    private boolean handleActiveUserOfQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        int count = recommendationsRepository.getActiveTransactionCountForProductType(userId, productType);
        boolean isActiveUser = count >= 5;
        return negate ? !isActiveUser : isActiveUser;
    }

    private boolean handleTransactionSumCompareQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        String transactionType = arguments.get(1).toUpperCase();
        String comparisonOperator = arguments.get(2);
        int threshold = Integer.parseInt(arguments.get(3));

        int totalTransactionSum = recommendationsRepository.getTransactionSum(userId, productType, transactionType);

        boolean result = compareSum(totalTransactionSum, threshold, comparisonOperator);
        return negate ? !result : result;
    }

    private boolean handleTransactionSumCompareDepositWithdrawQuery(String userId, List<String> arguments, boolean negate) {
        String productType = arguments.get(0).toUpperCase();
        String comparisonOperator = arguments.get(1);

        int depositSum = recommendationsRepository.getTransactionSum(userId, productType, "DEPOSIT");
        int withdrawSum = recommendationsRepository.getTransactionSum(userId, productType, "WITHDRAW");

        boolean result = compareSum(depositSum, withdrawSum, comparisonOperator);
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