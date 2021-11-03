package org.andreidodu.tbot.util;

import org.andreidodu.tbot.dto.LogRowDTO;
import org.andreidodu.tbot.dto.LogWrapperDTO;
import org.apache.commons.io.input.TailerListenerAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyTailerListener extends TailerListenerAdapter {

	private LogWrapperDTO logWrapper;

	private static final int MAX_NUM_ROWS = 100;

	public MyTailerListener(LogWrapperDTO logWrapper) {
		this.logWrapper = logWrapper;
	}

	@Override
	public void handle(String line) {
		if (this.logWrapper.getRows().size() >= MAX_NUM_ROWS) {
			this.logWrapper.getRows().remove(0);
		}
		LogRowDTO row = new LogRowDTO();
		row.setContent(line);
		this.logWrapper.getRows().add(row);
	}

}
