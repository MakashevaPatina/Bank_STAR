package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicRulesRepository extends JpaRepository<DynamicRule, Long> {
}