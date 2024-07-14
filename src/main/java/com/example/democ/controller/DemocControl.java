package com.example.democ.controller;

import com.example.democ.config.NotificationBotConfig;
import com.example.democ.service.DemocLogic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DemocControl implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String token;
    private final DemocLogic democLogic;
    final NotificationBotConfig botConfig;

    static final String HELP_TEXT = "Этот бот создан для помощи в тренировках\n\n" +
    "Вы можете выбрать упражнения, в которых хотите развиваться, либо посчитать суточную норму калорий\n\n" +
    "Нажмите /start чтобы начать работу с ботом\n\n" + "Нажмите /help чтобы получить данное сообщение\n\n" +
    "Нажмите /changetime чтобы изменить время напоминаний\n\n";

    public DemocControl(@Value("${botToken}") String token, TelegramClient telegramClient, DemocLogic democLogic, NotificationBotConfig botConfig) {
        this.telegramClient = telegramClient;
        this.token = token;
        this.democLogic = democLogic;
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получить стартовое сообщение"));
        listOfCommands.add(new BotCommand("/help", "получить описание бота"));
        listOfCommands.add(new BotCommand("/changetime", "изменить время напоминаний"));
        try{
            telegramClient.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
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
        } else if (message_text.equals("/help")) {
            Send(HELP_TEXT, update.getMessage().getChatId());
        } else if (update.hasCallbackQuery()) {

            System.out.println(update.getCallbackQuery().getData() + ":" + update.getCallbackQuery().getMessage().getChatId() + ":" + update.getCallbackQuery().getMessage().getChat().getUserName() + ":" + update.getCallbackQuery().getMessage().getChat().getFirstName());
            String call_data = update.getCallbackQuery().getData();
            switch (call_data) {
                case "Тренировки":
                    answer = "Выберите программу тренировок:";
                    String[] bca = {"Отжимания", "Подтягивания", "Брусья"};
                    SendWithButtons(answer, vec(3, bca, bca), update.getCallbackQuery().getMessage().getChatId());
                    break;
                case "Калории":
                    answer = "Введите параметры для подсчета калорий.\n Для начала выберите пол:";
                    String[] gender = {"Мужчина", "Женщина"};
                    SendWithButtons(answer, vec(2, gender, gender), update.getCallbackQuery().getMessage().getChatId());
                    break;
                case "Мужчина":
                    answer = "Введите вес(кг) и рост(см) и возраст:";
                    Send(answer, update.getCallbackQuery().getMessage().getChatId());
                    if (update.hasMessage()) {
                        String[] weightAndheightM = new String[]{update.getMessage().getText()};
                        System.out.println(weightAndheightM);
                        if (weightAndheightM.length == 3 && weightAndheightM[0].matches("\\d+")
                                && weightAndheightM[1].matches("\\d+") && weightAndheightM[2].matches("\\d+")) {
                            String resultNum = String.format("%.1f", 655, 1 + 9, 563 * Integer.parseInt(weightAndheightM[0]) + 1, 85 * Integer.parseInt(weightAndheightM[1]) - 4, 676 * Integer.parseInt(weightAndheightM[2]));
                            System.out.println(resultNum);
                        }
                        answer = "Введите вес(кг):";


                    }
                    break;
                case "Женщина":
                    answer = "Введите рост(см):";
                    Send(answer, update.getCallbackQuery().getMessage().getChatId());
                    String heightF = update.getMessage().getText();
                    answer = "Введите вес(кг):";
                    String weightF = update.getMessage().getText();
                    System.out.println(weightF);
                    break;
            }
        //}else if
            /*} else {
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
            }*/
        }
    }
    void Send(String message_text,long chat_id){
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
    void SendWithButtons(String message,Vector<InlineKeyboardButton> vector,long chatId){
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
    Vector<InlineKeyboardButton> vec(int size,String[] text,String[] callBack){
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
}
