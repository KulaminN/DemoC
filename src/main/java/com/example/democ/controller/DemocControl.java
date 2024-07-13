package com.example.democ.controller;

import com.example.democ.service.DemocLogic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DemocControl implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String token;
    private final DemocLogic democLogic;

    public DemocControl(@Value("${botToken}") String token, TelegramClient telegramClient, DemocLogic democLogic) {
        this.telegramClient = telegramClient;
        this.token = token;
        this.democLogic = democLogic;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        String answer = "";
        String message_text = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            message_text = update.getMessage().getText();
            String userName = update.getMessage().getFrom().getUserName();
        }

        if (message_text.equals("/start")) {
            answer = "Привет. Пожалуйста выбери категорию.";
            SendMessage message = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text(answer)
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Тренировки")
                                            .callbackData("Тренировки")
                                            .build())
                            )
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Калории")
                                            .callbackData("Калории")
                                            .build())
                            ).build())
                    .build();
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            System.out.println(update.hasCallbackQuery());
        } else if (update.hasCallbackQuery()) {

            System.out.println(update.getCallbackQuery().getData() + ":" + update.getCallbackQuery().getMessage().getChatId() + ":" + update.getCallbackQuery().getMessage().getChat().getUserName() + ":" + update.getCallbackQuery().getMessage().getChat().getFirstName());

            String call_data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (call_data) {

                case "Тренировки" -> {
                    answer = "Выберите программу тренировок:";
                    String[] bca = {"Отжимания", "Подтягивания", "Брусья"};
                    SendWithButtons(answer, vec(3, bca, bca), update.getCallbackQuery().getMessage().getChatId());
                }
                case "Калории" -> {
                    answer = "Введите параметры для подсчета калорий.\n Рост(см):";
                    Send(answer, update.getCallbackQuery().getMessage().getChatId());
                }
                case "Отжимания" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=rJCbLIYF",chatId);
                }
                case "Подтягивания" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=qcxo1zvy",chatId);
                }
                case "Брусья" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=JFDNjGaV",chatId);
                }
            }
        } else {
            String regex = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})\\s(.+)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message_text);

            if (matcher.matches()) {
                String dateTime = matcher.group(1);
                String message = matcher.group(2);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                democLogic.createNotification(update.getMessage().getChatId(), LocalDateTime.parse(dateTime, dateFormatter), message);


                System.out.println("Дата = " + dateTime + " Сообщение = " + message);
            } else {
                answer = "неверный формат. Пришли сообщение формата: дд.мм.гггг чч:мм текст напоминания";
                SendMessage message = SendMessage
                        .builder()
                        .chatId(update.getMessage().getChatId())
                        .text(answer)
                        .build();

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void Send(String message_text, long chat_id) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(message_text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    void SendWithButtons(String message, Vector<InlineKeyboardButton> vector, long chatId) {
        SendMessage messag = SendMessage
                .builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(new InlineKeyboardRow(vector))
                        .build())
                .build();
        try {
            telegramClient.execute(messag);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    Vector<InlineKeyboardButton> vec(int size, String[] text, String[] callBack) {
        Vector<InlineKeyboardButton> vector = new Vector<>();
        for (int i = 0; i < size; i++) {
            vector.add(InlineKeyboardButton
                    .builder()
                    .text(text[i])
                    .callbackData(callBack[i])
                    .build());
        }
        return vector;
    }

    public String sendPhoto(String photoUrl, Long chatId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://api.telegram.org/bot" + getBotToken() + "/sendPhoto?chat_id=" + chatId + "&photo=" + photoUrl;
        String response = restTemplate.postForObject(apiUrl, null, String.class);
        return response;
    }
}
