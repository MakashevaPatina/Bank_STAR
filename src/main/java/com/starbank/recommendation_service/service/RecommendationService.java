package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.dto.Transaction;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.repository.TransactionRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import com.starbank.recommendation_service.rules.dynamic.Condition;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;
    private final DynamicRulesRepository dynamicRulesRepository;
    private final RecommendationsRepository recommendationsRepository;
    public final TransactionRepository transactionRepository;
    @Autowired
    public RecommendationService(List<RecommendationRuleSet> ruleSets, DynamicRulesRepository dynamicRulesRepository, RecommendationsRepository recommendationsRepository, TransactionRepository transactionRepository) {
        this.ruleSets = ruleSets;
        this.dynamicRulesRepository = dynamicRulesRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.transactionRepository = transactionRepository;
    }

    public RecommendationResponse getRecommendations(String userId) {
        List<DynamicRule> dynamicRules = dynamicRulesRepository.findAll();
        List<RecommendationDTO> recommendations = ruleSets.stream()
                .map(rule -> rule.getRecommendation(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        for (DynamicRule rule : dynamicRules) {
            boolean isApplicable = checkDynamicRuleConditions(rule, userId);

            if (!isApplicable) {
                recommendations.add(new RecommendationDTO(rule.getProductName(), rule.getId().toString(), rule.getProductText()));
            }
        }
        return new RecommendationResponse(recommendations, userId);
    }

    private boolean checkDynamicRuleConditions(DynamicRule rule, String userId) {
        for (Condition condition : rule.getConditions()) {
            boolean conditionMet = processQuery(userId, condition.getQuery(), condition.getArguments(), condition.isNegate());
            if (!conditionMet) {
                return false;
            }
        }
        return true;
    }

    public boolean processQuery(String userId, String queryType, List<String> arguments, boolean negate) {
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
        Transaction.ProductType productType = Transaction.ProductType.valueOf(arguments.get(0).toUpperCase());

        boolean isUser = transactionRepository.hasTransactionForProductType(userId, productType);

        return negate ? !isUser : isUser;
    }

    private boolean handleActiveUserOfQuery(String userId, List<String> arguments, boolean negate) {
        Transaction.ProductType productType = Transaction.ProductType.valueOf(arguments.get(0).toUpperCase());

        boolean isActiveUser = transactionRepository.countTransactionsForProductType(userId, productType) >= 5;

        return negate ? !isActiveUser : isActiveUser;
    }

    private boolean handleTransactionSumCompareQuery(String userId, List<String> arguments, boolean negate) {
        Transaction.ProductType productType = Transaction.ProductType.valueOf(arguments.get(0).toUpperCase());
        Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(arguments.get(1).toUpperCase());
        String comparisonOperator = arguments.get(2);
        int threshold = Integer.parseInt(arguments.get(3));


        int totalTransactionSum = transactionRepository.getTransactionSumForProductTypeAndTransactionType(userId, productType, transactionType);

        boolean result = compareSum(totalTransactionSum, threshold, comparisonOperator);

        return negate ? !result : result;
    }

    private boolean handleTransactionSumCompareDepositWithdrawQuery(String userId, List<String> arguments, boolean negate) {
        Transaction.ProductType productType = Transaction.ProductType.valueOf(arguments.get(0).toUpperCase());
        String comparisonOperator = arguments.get(1);

        int depositSum = transactionRepository.getTransactionSumForProductTypeAndTransactionType(userId, productType, Transaction.TransactionType.DEPOSIT);
        int withdrawSum = transactionRepository.getTransactionSumForProductTypeAndTransactionType(userId, productType, Transaction.TransactionType.WITHDRAW);

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