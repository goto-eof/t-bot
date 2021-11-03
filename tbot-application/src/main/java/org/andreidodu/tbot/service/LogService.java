package org.andreidodu.tbot.service;

import java.util.List;

import org.andreidodu.tbot.dto.LogRowDTO;

public interface LogService {

	List<LogRowDTO> retrieveLog(String codiceBot);

	List<LogRowDTO> retrieveLog();

}
