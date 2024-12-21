package com.starbank.recommendation_service.telegramBot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import com.starbank.recommendation_service.telegramBot.commands.CommandRecommendation;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.starbank.recommendation_service.telegramBot.service.CommandService;


import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;


    @Autowired
    private CommandRecommendation commandRecommendation;

    @Autowired
    private CommandService commandService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null) {
                Long chatID = update.message().chat().id();
                logger.info("Processing update: {}", update);

                if (update.message().text() != null && update.message().text().contains("/recommend")) {
                    commandRecommendation.handle(update);
                } else if (update.message().text() != null) {
                    commandService.handleCommand(update);
                } else {
                    logger.warn("Received update without text message: {}", update);
                }
            } else {
                logger.warn("Received update without message: {}", update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
