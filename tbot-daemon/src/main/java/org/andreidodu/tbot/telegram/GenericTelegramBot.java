package org.andreidodu.tbot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericTelegramBot extends TelegramLongPollingBot implements TelegramBot {

	public final static String TELEGRAM_BOT_TYPE = "GENERIC";

	private String botUsername;
	private String botToken;

	public GenericTelegramBot() {
		super();
	}

	public GenericTelegramBot(String token, String botName) {
		super();
		this.botUsername = botName;
		this.botToken = token;
	}

	/**
	 * Method for creating a message and sending it.
	 *
	 * @param chatId  chat id
	 * @param message The String that you want to send as a message.
	 */
	public synchronized void sendMsg(String chatId, String messageStr) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chatId).setText(messageStr);
		message.enableHtml(true);
		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public synchronized void sendHTMLMsg(String chatId, String messageStr, boolean enableSilent, boolean disableWebPagePreview) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chatId).setText(messageStr);
		message.enableHtml(true);
		if (enableSilent) {
			message.disableNotification();
		}
		if (disableWebPagePreview) {
			message.disableWebPagePreview();
		}
		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns the bot's name, which was specified during registration.
	 *
	 * @return bot name
	 */
	@Override
	public String getBotUsername() {
		return this.botUsername;
	}

	/**
	 * This method returns the bot's token for communicating with the Telegram
	 * server
	 *
	 * @return the bot's token
	 */
	@Override
	public String getBotToken() {
		return this.botToken;
	}

	@Override
	public String getType() {
		return TELEGRAM_BOT_TYPE;
	}

	@Override
	public void onUpdateReceived(Update update) {
		// do nothing
		log.info("#############DO NOTHING");
	}


}