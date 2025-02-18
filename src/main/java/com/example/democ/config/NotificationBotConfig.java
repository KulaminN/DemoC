package com.example.democ.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class NotificationBotConfig {
    @Bean
    public TelegramClient telegramClient(@Value("${botToken}") String token){
        return new OkHttpTelegramClient(token);
    }
}
