package com.example.democ.controller;

import com.example.democ.service.DemocLogic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

@RestController
public class DemocControl implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    static int per = -1;
    private final TelegramClient telegramClient;
    private final String token;
    private final DemocLogic democLogic;
    public static int date = 0;
    static Long week = 0L;

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
        String Time = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            message_text = update.getMessage().getText();
            String userName = update.getMessage().getFrom().getUserName();
        }
        if (message_text.equals("/start")) {
            answer = "Привет. Пожалуйста выбери категорию.";
            String[] Buttons = {"Тренировки", "Калории"};
            Send(answer, vec(2, Buttons, Buttons), update.getMessage().getChatId());
            System.out.println(update.hasCallbackQuery());
        } else if (update.hasCallbackQuery()) {

            System.out.println(update.getCallbackQuery().getData() + ":" + update.getCallbackQuery().getMessage().getChatId() + ":" + update.getCallbackQuery().getMessage().getChat().getUserName() + ":" + update.getCallbackQuery().getMessage().getChat().getFirstName());

            String call_data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (call_data) {

                case "Тренировки" -> {
                    answer = "Выберите программу тренировок:";
                    String[] bca = {"Отжимания", "Подтягивания", "Брусья", "Назад"};
                    Edit(answer, update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), vec(4, bca, bca));
                }
                case "Калории" -> {
                    answer = "Введите параметры для подсчета калорий.\n Рост(см):";
                    Send(answer, update.getCallbackQuery().getMessage().getChatId());
                }
                case "Отжимания" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=rJCbLIYF", chatId);
                    Send("Вот ваш план тенировок. С какой недели хотели бы начать? (1-15)", chatId);
                    per = 1;
                }
                case "Брусья" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=qcxo1zvy", chatId);
                    Send("Вот ваш план тенировок. С какой недели хотели бы начать? (1-17)", chatId);
                    per = 3;
                }
                case "Подтягивания" -> {
                    sendPhoto("https://post-images.org/photo-page.php?photo=JFDNjGaV", chatId);
                    Send("Вот ваш план тенировок. С какой недели хотели бы начать? (1-30)", chatId);
                    per = 2;
                }
                case "Назад" -> {
                    String[] Butto = {"Тренировки", "Калории"};
                    answer = "Привет. Пожалуйста выбери категорию.";
                    Edit(answer, update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), vec(2, Butto, Butto));
                }
            }
        } else if (per != -1 && (update.getMessage().getText().matches("\\d") || update.getMessage().getText().matches("\\d{2}"))) {
            for (int i = 0; i < 1; i++) {
                week = Long.valueOf(update.getMessage().getText());
            }

            switch (per) {
                case 1 -> {
                    Send("Вам нужно будет выполнять необходимое количество отжиманий 1 раз в 2 дня. \nУстановите время тренировки в формате (чч:мм)", update.getMessage().getChatId());

                }
                case 2 -> {
                    Send("Вам нужно будет выполнять необходимое количество подтягиваний 1 раз в 2 дня. \nУстановите время тренировки в формате (чч:мм)", update.getMessage().getChatId());
                }
                case 3 -> {
                    Send("Вам нужно будет выполнять необходимое количество отжиманий на брусьях 1 раз в 2 дня. \nУстановите время тренировки в формате (чч:мм)", update.getMessage().getChatId());
                }
            }
        } else {
            System.out.println("asgojsdjgld");
            message_text = update.getMessage().getText();
            String regex = "(\\d{2}:\\d{2})";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message_text);
            System.out.println(matcher.matches());
            if (matcher.matches()) {
                Time = matcher.group();
                switch (per) {
                    case 1 -> message_text = "Отжимания";
                    case 2 -> message_text = "Подтягивания";
                    case 3 -> message_text = "Брусья";
                }
                System.out.println(Time);
                String newTime = LocalDate.now() + " " + Time;

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(newTime, dateFormatter);
                System.out.println(dateTime);
                dateTime = dateTime.minusHours(1);
                date = dateTime.getDayOfMonth();
                Send("Время установлено\nВам будут приходить уведомления за 1 час до тренировки", update.getMessage().getChatId());
            }
            String finalTime = Time;
            String finalMessage_text = message_text;
            new Thread(()->{
                while (true){
                    if (date == LocalDateTime.now().getDayOfMonth()) {
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        String newTime = LocalDate.now() + " " + finalTime;
                        LocalDateTime dateTime = LocalDateTime.parse(newTime, dateFormatter);
                        dateTime = dateTime.minusHours(1);
                        System.out.println(dateTime);
                        democLogic.createNotification(update.getMessage().getChatId(), dateTime, finalMessage_text, week, date);
                        dateTime = dateTime.plusDays(2);
                        date += 2;
                        DemocLogic.flag = false;
                        System.out.println("Hello");
                    }
                }
            }).start();

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

    void Edit(String message_text, long chat_id, Integer message_id, Vector<InlineKeyboardButton> vector) {
        EditMessageText new_message = EditMessageText
                .builder()
                .chatId(chat_id)
                .messageId(toIntExact(message_id))
                .text(message_text)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(new InlineKeyboardRow(vector))
                        .build())
                .build();
        try {
            telegramClient.execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void Edit(String message_text, long chat_id, Integer message_id) {
        EditMessageText new_message = EditMessageText
                .builder()
                .chatId(chat_id)
                .messageId(toIntExact(message_id))
                .text(message_text)
                .build();
        try {
            telegramClient.execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void Send(String message, Vector<InlineKeyboardButton> vector, long chatId) {
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
