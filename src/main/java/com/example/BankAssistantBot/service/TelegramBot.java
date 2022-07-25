package com.example.BankAssistantBot.service;

import com.example.BankAssistantBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

	final BotConfig botConfig;

	static final String HELP_TEXT = "Help message";

	public TelegramBot(BotConfig botConfig) {
		this.botConfig = botConfig;
		List<BotCommand> listOfCommands = new ArrayList<>();
		listOfCommands.add(new BotCommand("/start", "Get a welcome message"));
		listOfCommands.add(new BotCommand("/mydata", "Get your data stored"));
		listOfCommands.add(new BotCommand("/deletedata", "Delete my data"));
		listOfCommands.add(new BotCommand("/help", "Info of bots commands"));
		listOfCommands.add(new BotCommand("/settings", "Set you preferences"));
		try {
			this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
		}
		catch (TelegramApiException e) {
			log.error("Error setting bots commands list: " + e.getMessage());
		}
	}

	@Override
	public String getBotUsername() {
		return botConfig.getBotName();
	}

	@Override
	public String getBotToken() {
		return botConfig.getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {
		if(update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();
			switch (messageText) {
				case "/start":
					startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
					break;
				case "/help":
					sendMessage(chatId, HELP_TEXT);
				default:
					sendMessage(chatId, "Sorry, this command was not recognized(");
					break;
			}
		}
	}

	private void startCommandReceived(long chatId, String name) {
		String answer = "Hi, " + name + ", nice to meet you!";
		log.info("Replied to user with name " + name);
		sendMessage(chatId, answer);
	}

	private void sendMessage(long chatId, String textToSend) {
		SendMessage message = new SendMessage();
		message.setChatId(String.valueOf(chatId));
		message.setText(textToSend);
		try
		{
			execute(message);
		}
		catch (TelegramApiException e) {
			log.error("Error occurred: " + e.getMessage());
		}
	}
}
