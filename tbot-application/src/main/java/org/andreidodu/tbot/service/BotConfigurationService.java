package org.andreidodu.tbot.service;

import java.util.List;

import org.andreidodu.tbot.dto.BotDTO;

public interface BotConfigurationService {

	List<BotDTO> getAllBots();

	BotDTO newBot();

	Boolean delete(String codiceBot);

	BotDTO update(String codiceBot, BotDTO botDTO);

	Boolean reloadConfiguration(String codiceBot, Boolean reload);

	Boolean reloadAllConfiguration(String codiceBot, Boolean reload);

}
