package org.andreidodu.tbot.telegram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TelegramUtil {

	private static String FILENAME_EXT = ".json";

	private Map<String, GenericTelegramBot> telegramBots = new ConcurrentHashMap<>();

	public TelegramUtil() {
		ApiContextInitializer.init();
	}

	public void initializeBot(ConfigurationDTO configuration, String telegramBotType) {

		try {
			GenericTelegramBot bot = null;

			Boolean isOnUpdateReceivedDisabled = Boolean
							.valueOf(BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_IS_ON_UPDATE_RECEIVED_DISABLED)));
			log.info("#################isOnUpdateReceivedDisabled: " + isOnUpdateReceivedDisabled);

			final String codiceBot = configuration.getCodiceBot();
			/*
			 * if (telegramBotType.equalsIgnoreCase(MrJohnBlackTelegramBot.TELEGRAM_BOT_TYPE)) { bot = new MrJohnBlackTelegramBot(botToken, configPrefix,
			 * !isOnUpdateReceivedDisabled); } else
			 */
			if (telegramBotType.equalsIgnoreCase(RssTelegramBot.TELEGRAM_BOT_TYPE)) {
				String path = configuration.loadCustomValue(WorkerConst.CS_SAVE_PATH);
				String filename = path + codiceBot + ".messages" + FILENAME_EXT;
				log.info("@" + codiceBot + " - " + filename);
				String welcomeMessage = configuration.loadCustomValue(WorkerConst.CS_BOT_WELCOME_MESSAGE,
								"Ciao,\nScrivi qui i tuoi suggerimenti, le critiche o i problemi riscontrati per migliorare il canale.");
				String thanksMessage = configuration.loadCustomValue(WorkerConst.CS_BOT_THANKS_MESSAGE, "Grazie per il tuo messaggio.");

				bot = new RssTelegramBot(configuration.loadCustomValue(WorkerConst.CS_BOT_TOKEN), codiceBot, filename, welcomeMessage, thanksMessage,
								!isOnUpdateReceivedDisabled);
			} else {
				// bot = new GenericTelegramBot(botToken, configPrefix);
			}

			TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
			telegramBotsApi.registerBot(bot);

			this.telegramBots.put(codiceBot, bot);
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
			log.error(e.toString());
			log.info("Errore inizializzazione bot TELEGRAM: " + e.getMessage());
		}

	}

	public void sendMessage(String botPrefix, String chatId, String message) {
		log.info("invio del messaggio in corso: " + message);
		getBot(botPrefix).sendMsg(chatId, message);
		log.info("messaggio inviato");
	}

	public void sendHTMLMessage(String botPrefix, String chatId, String message, boolean enableSilent, boolean disableWebPagePreview) {
		log.info("invio del messaggio in corso: " + message);
		getBot(botPrefix).sendHTMLMsg(chatId, message, enableSilent, disableWebPagePreview);
		log.info("messaggio inviato");
	}

	private GenericTelegramBot getBot(String botPrefix) {
		return this.telegramBots.get(botPrefix);
	}

}
