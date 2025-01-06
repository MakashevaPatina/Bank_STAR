package com.starbank.recommendation_service.telegramBot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.starbank.recommendation_service.dto.RecommendationDTO;
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
    private final Logger logger = LoggerFactory.getLogger(CommandRecommendation.class);

    @Override
    public void handle(Update update) {
        try {
            Long chatID = update.message().chat().id();

            String clientUserName = update.message().text().substring(update.message().text().indexOf(" ") + 1);

            String clientId = null;
            try {
                clientId = jdbcTemplate.queryForObject(
                        "SELECT id FROM users WHERE username = ?",
                        String.class,
                        clientUserName
                );
            } catch (ClientNotFoundException e) {
                logger.error("Ошибка", e);
                SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Клиент не найден");
                telegramBot.execute(sendMessage);
            }

            String clientFullName = null;
            try {
                clientFullName = jdbcTemplate.queryForObject(
                        "SELECT first_name FROM users WHERE id = ?",
                        String.class,
                        clientId
                );
                clientFullName = clientFullName + " " + jdbcTemplate.queryForObject(
                        "SELECT last_name FROM users WHERE id = ?",
                        String.class,
                        clientId
                );
            } catch (ClientNotFoundException e) {
                logger.error("Ошибка", e);
                SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Клиент не найден");
                telegramBot.execute(sendMessage);
            }

            RecommendationResponse recommendationResponse = recommendationService.getRecommendations(clientId);
            String recommendations = null;
            for (RecommendationDTO recommendation : recommendationResponse.getRecommendations()) {
                recommendations += recommendation.getName() + "\n" + recommendation.getText() + "\n";
            }

            String text = String.format("Здравствуйте %s.\nНовые продукты для вас: %s ", clientFullName, recommendations);
            SendMessage sendMessage = new SendMessage(chatID, text);

            telegramBot.execute(sendMessage);
            logger.info("Sent message: {}", text);

        } catch (Exception e) {
            logger.error("Error handling recommendation: ", e);
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Произошла ошибка при обработке запроса. Попробуйте позже.");
            telegramBot.execute(sendMessage);
        }
    }

    @Override
    public String getCommand() {
        return "/recommend";
    }
}
