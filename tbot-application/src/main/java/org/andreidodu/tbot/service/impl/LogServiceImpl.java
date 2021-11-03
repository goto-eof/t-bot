package org.andreidodu.tbot.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.andreidodu.tbot.dto.LogRowDTO;
import org.andreidodu.tbot.dto.LogWrapperDTO;
import org.andreidodu.tbot.service.LogService;
import org.andreidodu.tbot.util.MyTailerListener;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private Environment env;

	private String fileName;

	private LogWrapperDTO logWrapper = new LogWrapperDTO();

	TailerListener listener;
	Tailer tailer;

	@PostConstruct
	private void init() {
		this.fileName = env.getProperty("application.configuration.daemon.logging.file.name");
		log.debug("Il percorso del file è: " + this.fileName);
	}

	private void initTailer() {
		File file = new File(this.fileName);
		if (file.exists()) {
			this.listener = new MyTailerListener(this.logWrapper);
			this.tailer = new Tailer(file, listener, 500);
			this.tailer.run();
		}
	}

	@Override
	public List<LogRowDTO> retrieveLog(String codiceBot) {
		if (this.listener == null) {
			this.initTailer();
		}
		if (this.tailer == null) {
			log.debug("Il tailer è null");
		}
		List<LogRowDTO> result = new ArrayList<>();
		this.logWrapper.getRows().forEach(row -> {
			if (row.getContent().indexOf(codiceBot) > -1) {
				result.add(row);
			}
		});
		this.logWrapper.getRows().clear();
		return result;
	}

	@Override
	public List<LogRowDTO> retrieveLog() {
		if (this.listener == null) {
			this.initTailer();
		}
		if (this.tailer == null) {
			log.debug("Il tailer è null");
		}
		List<LogRowDTO> result = new ArrayList<>();
		this.logWrapper.getRows().forEach(row -> result.add(row));
		this.logWrapper.getRows().clear();
		return result;
	}

}
