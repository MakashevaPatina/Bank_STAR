package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.dto.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByProductType(Transaction.ProductType productType);

    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.userId = :userId AND t.productType = :productType")
    boolean hasTransactionForProductType(@Param("userId") String userId, @Param("productType") Transaction.ProductType productType);


    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.productType = :productType")
    long countTransactionsForProductType(@Param("userId") String userId, @Param("productType") Transaction.ProductType productType);

    // Получить сумму транзакций пользователя по типу продукта и транзакции
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.productType = :productType AND t.transactionType = :transactionType")
    Integer getTransactionSumForProductTypeAndTransactionType(
            @Param("userId") String userId,
            @Param("productType") Transaction.ProductType productType,
            @Param("transactionType") Transaction.TransactionType transactionType);
}
