package com.starbank.recommendation_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Правило не найдено")
public class DynamicRuleNotFoundException extends RuntimeException{
    public DynamicRuleNotFoundException(long id) {
        super("%s правило не найдено".formatted(id));
    }
}


