package org.andreidodu.tbot.workers;

import java.util.List;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.service.ConfigurationService;
import org.andreidodu.tbot.telegram.TelegramUtil;
import org.andreidodu.tbot.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomBot1Worker implements BotWorker {

	private final static String WORKER_TYPE = "custom_bot_1";

	@Autowired
	private TelegramUtil telegramUtil;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public Runnable getRunnable(String configPrefix) {
		return () -> {
			try {
				ConfigurationDTO configuration = this.configurationService.loadConfiguration(configPrefix);
				log.info("----------------------------------------------------------------------------------");
				log.info("initializing bot [" + configPrefix + "]...");
				log.info("----------------------------------------------------------------------------------");
				if (BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_INACTIVE))) {
					log.info("premature exit for the bot [" + configPrefix + "]");
					log.info("----------------------------------------------------------------------------------");
					log.info("bot [" + configPrefix + "] died");
					log.info("----------------------------------------------------------------------------------");
					return;
				}

				List<String> chatIds = StringUtils.asList(configuration.loadCustomValue(WorkerConst.CS_CHAT_ID), Pattern.quote("|"));
				log.info("chatId: " + chatIds);

				String botToken = configuration.loadCustomValue(WorkerConst.CS_BOT_TOKEN);
				String botType = configuration.loadCustomValue(WorkerConst.CS_BOT_TYPE);
//				this.telegramUtil.initializeBot(botType, "MR_JOHN_BLACK", configPrefix, botToken);

				log.info("TelegramBot created [" + configPrefix + "]");

				log.info("----------------------------------------------------------------------------------");
				log.info("bot [" + configPrefix + "] initialized successfully!");
				log.info("listening...");
				log.info("----------------------------------------------------------------------------------");
			} catch (RuntimeException e) {
				e.printStackTrace();
				log.error("Huston, we have an Exception", e);
			}
		};
	}

	@Override
	public String getType() {
		return WORKER_TYPE;
	}

}
