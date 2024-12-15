package com.starbank.recommendation_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Клиент не найден")
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String id) {
        super("%s клиент не найден".formatted(id));
    }
}


