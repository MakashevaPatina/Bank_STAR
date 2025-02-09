package com.starbank.recommendation_service.telegramBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.starbank.recommendation_service.telegramBot.commands.CommandHandler;
import com.starbank.recommendation_service.telegramBot.commands.CommandRecommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandService {
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(CommandRecommendation.class);

    @Autowired
    private TelegramBot telegramBot;


    @Autowired
    public CommandService(List<CommandHandler> handlers) {
        handlers.forEach(handler -> commandHandlers.put(handler.getCommand(), handler));
    }

    public void handleCommand(Update update) {
        String command = update.message().text();
        CommandHandler handler = commandHandlers.get(command);
        if (handler != null) {
            handler.handle(update);
        } else {
            Long chatID = update.message().chat().id();
            SendMessage sendMessage = new SendMessage(chatID, "Unsupported command: " + command);
            telegramBot.execute(sendMessage);
            logger.info("Unsupported command: {}", command);
        }
    }

    public Map<String, CommandHandler> getCommandHandlers() {
        return commandHandlers;
    }
}
