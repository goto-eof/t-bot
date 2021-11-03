package org.andreidodu.tbot.service;

import org.andreidodu.tbot.dto.BotQstStatusDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;

public interface StatusService {

	BotQstStatusDTO insertOrUpdate(UrlInternalDTO urlInternal, String key, String value);

	String get(UrlInternalDTO urlInternal, String key);

}
