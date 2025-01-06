package com.starbank.recommendation_service.telegramBot.commands;

import com.pengrad.telegrambot.model.Update;

public interface CommandHandler {
    void handle(Update update);

    String getCommand();
}

