package org.andreidodu.tbot.telegram;

import org.andreidodu.tbot.dto.Message;
import org.andreidodu.tbot.dto.MessageStorage;
import org.andreidodu.tbot.util.FileUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssTelegramBot extends GenericTelegramBot {

	public static final String TELEGRAM_BOT_TYPE = "RSS";

	private String filename;
	private String welcomeMessage;
	private String thanksMessage;
	private MessageStorage messageStorage = new MessageStorage();
	private boolean isOnUpdateReceivedEnabled;

	public RssTelegramBot(String token, String botName, String filename, String welcomeMessage, String thanksMessage, boolean isOnUpdateReceivedEnabled) {
		super(token, botName);
		this.filename = filename;
		this.welcomeMessage = welcomeMessage;
		this.thanksMessage = thanksMessage;
		this.isOnUpdateReceivedEnabled = isOnUpdateReceivedEnabled;
	}

	@Override
	public String getType() {
		return TELEGRAM_BOT_TYPE;
	}

	@Override
	public void onUpdateReceived(Update update) {
		logga("update message received");
		if (this.isOnUpdateReceivedEnabled && update.hasMessage() && update.getMessage().hasText()) {
			this.messageStorage = FileUtil.load(this.filename, MessageStorage.class);
			if (this.messageStorage == null) {
				this.messageStorage = new MessageStorage();
			}

			String msg = update.getMessage().getText();
			String name = buildUsername(update);
			if (userStoredMessagesLessThanX(name, 50)) {
				this.messageStorage.getMessages().add(buildMessage(name, msg));
				FileUtil.save(this.filename, this.messageStorage);
			}
			logga("ho ricevuto il seguente messaggio: [" + msg + "] from [" + name + "]");

			String responseMessage = this.thanksMessage;
			if ("/start".equalsIgnoreCase(msg)) {
				responseMessage = this.welcomeMessage;
			}
			SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(responseMessage);
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
				log.error(e.toString());
				logga("errore strano: " + e.getMessage());
			}
		}
	}

	public void logga(String str) {
		log.info(String.format("[%s]-------------[%s]", super.getBotUsername(), str));
	}

	private boolean userStoredMessagesLessThanX(String name, int x) {
		return this.messageStorage.getMessages().stream().filter(message -> name.equalsIgnoreCase(message.getUsername().toLowerCase())).count() < x;
	}

	private Message buildMessage(String name, String msg) {
		Message result = new Message();
		result.setUsername(name);
		result.setMessage(msg);
		return result;
	}

	private String buildUsername(Update update) {
		String firstName = update.getMessage().getFrom().getFirstName();
		firstName = firstName == null ? "" : firstName + " ";
		String lastName = update.getMessage().getFrom().getLastName();
		lastName = lastName == null ? "" : lastName + " ";
		String username = update.getMessage().getFrom().getUserName();
		username = username == null ? "" : " (" + username + ")";
		Integer id = update.getMessage().getFrom().getId();
		String idd = id == null ? "" : "[" + id + "]";
		String lang = update.getMessage().getFrom().getLanguageCode();
		String languageCode = lang == null ? "" : "[" + lang + "]";
		return firstName + " " + lastName + username + idd + languageCode;
	}

}