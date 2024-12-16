package com.starbank.recommendation_service.telegramBot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.exception.ClientNotFoundException;
import com.starbank.recommendation_service.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommandRecommendation implements CommandHandler {
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(CommandRecommendation.class);

    @Override
    public void handle(Update update) {
        try {
            Long chatID = update.message().chat().id();
            String clientId = update.message().text().substring(update.message().text().indexOf(" ") + 1);
            String clientFullName = jdbcTemplate.queryForObject(
                    "SELECT first_name FROM users WHERE id = ?",
                    String.class,
                    clientId
            );

            try {
                clientFullName = jdbcTemplate.queryForObject(
                        "SELECT first_name FROM users WHERE id = ?",
                        String.class,
                        clientId
                );
                clientFullName = clientFullName + jdbcTemplate.queryForObject(
                        "SELECT last_name FROM users WHERE id = ?",
                        String.class,
                        clientId
                );
            } catch (ClientNotFoundException e) {
                throw e;
            }

            RecommendationResponse recommendationResponse = recommendationService.getRecommendations(clientId);
            String text = "Рекомендации для клиента " + clientFullName + recommendationResponse;
            SendMessage sendMessage = new SendMessage(chatID, text);

            telegramBot.execute(sendMessage);
            logger.info("Sent message: {}", text);
            logger.info("User state set to DEFAULT for chatID: {}", chatID);

        } catch (Exception e) {
            logger.error("Error handling recommendation: ", e);
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Произошла ошибка при обработке запроса. Попробуйте позже.");
            telegramBot.execute(sendMessage);
        }
    }

    @Override
    public String getCommand() {
        return "/recommendation";
    }
}
