package org.andreidodu.tbot.telegram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MrJohnBlackTelegramBot extends GenericTelegramBot implements TelegramBot {

	public static final String TELEGRAM_BOT_TYPE = "MR_JOHN_BLACK";
	private boolean isOnUpdateReceivedEnabled;

	public MrJohnBlackTelegramBot(String botToken, String botName, boolean isOnUpdateReceivedEnabled) {
		super(botToken, botName);
		this.isOnUpdateReceivedEnabled = isOnUpdateReceivedEnabled;
	}

	/**
	 * Method for receiving messages.
	 *
	 * @param update Contains a message from the user.
	 */
	@Override
	public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
		if (this.isOnUpdateReceivedEnabled && update.hasMessage() && update.getMessage().hasText()) {
			String msg = update.getMessage().getText();
			String name = buildUsername(update);
			log.info("ho ricevuto il seguente messaggio: [" + msg + "] from [" + name + "]");
			String responseMessage = "";
			if (Arrays.asList("Ciao", "Hi").contains(msg)) {
				responseMessage = "Hello " + name;
			} else if (Arrays.asList("/start", "Help").contains(msg)) {
				responseMessage = "for now, are available the following commands: [hi][help][random quote]";
			} else if ("Random quote".equalsIgnoreCase(msg)) {
				try {
					RssReader reader = new RssReader();
					List<Item> items = reader.retrieveItems("https://www.brainyquote.com/link/quotebr.rss");
					int idx = new Random().nextInt(items.size());
					Item item = items.get(idx);
					responseMessage = "";
					if (item.getDescription().isPresent()) {
						responseMessage = item.getDescription().get();
						responseMessage += "\r\n";
					}
					if (item.getTitle().isPresent()) {
						responseMessage += item.getTitle().get();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("give me a hug".equalsIgnoreCase(msg)) {
				responseMessage = "🤗";
			} else {
				responseMessage = "uknown command [" + msg + "]";
			}
			SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
					.setChatId(update.getMessage().getChatId()).setText(responseMessage);
			setButtons(message);
			try {
				execute(message); // Call method to send the message
			} catch (TelegramApiException e) {
				e.printStackTrace();
				log.error(e.toString());
			}
		}
	}

	private String buildUsername(Update update) {
		String firstName = update.getMessage().getFrom().getFirstName();
		firstName = firstName == null ? "" : firstName + " ";
		String lastName = update.getMessage().getFrom().getLastName();
		lastName = lastName == null ? "" : lastName + " ";
		String username = update.getMessage().getFrom().getUserName();
		username = username == null ? "" : " (" + username + ")";
		return firstName + " " + lastName + username;
	}

	public synchronized void setButtons(SendMessage sendMessage) {

		// Create a keyboard
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		replyKeyboardMarkup.setSelective(Boolean.FALSE);
		replyKeyboardMarkup.setResizeKeyboard(Boolean.TRUE);
		replyKeyboardMarkup.setOneTimeKeyboard(Boolean.FALSE);

		// Create a list of keyboard rows
		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow row = new KeyboardRow();
		row.add(new KeyboardButton("Hi"));
		keyboard.add(row);

		row = new KeyboardRow();
		row.add(new KeyboardButton("Give me a hug"));
		row.add(new KeyboardButton("Random quote"));
		keyboard.add(row);
		// and assign this list to our keyboard
		replyKeyboardMarkup.setKeyboard(keyboard);
	}

	@Override
	public String getType() {
		return TELEGRAM_BOT_TYPE;
	}
}
