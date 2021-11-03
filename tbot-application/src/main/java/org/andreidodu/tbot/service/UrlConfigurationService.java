package org.andreidodu.tbot.service;

import java.util.List;

import org.andreidodu.tbot.dto.UrlDTO;

public interface UrlConfigurationService {

	List<UrlDTO> getAll(String codiceBot);

	UrlDTO newUrl(String codiceBot);

	Boolean delete(String codiceBot, String codiceQuestionario);

	UrlDTO update(String codiceBot, UrlDTO urlDTO);

	Boolean reloadConfiguration(String codiceBot, String codiceQuestionario, Boolean reload);

}
