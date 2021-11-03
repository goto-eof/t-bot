package org.andreidodu.tbot.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.BotQstDTO;
import org.andreidodu.tbot.service.BotQstService;
import org.andreidodu.tbot.service.ConfigurationService;
import org.andreidodu.tbot.workers.BotWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdurmont.emoji.EmojiParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThreadUtil {

	@Autowired
	private List<BotWorker> botWorkers;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private BotQstService botQstService;

	public Thread startNewThread(String codiceBot, boolean joinThread) {
		Thread thread = null;
		try {

			System.setProperty("jdk.httpclient.keepalive.timeout", "5"); // seconds
			System.setProperty("jdk.httpclient.connectionPoolSize", "1");

			String botType = this.configurationService.loadValueImmediately(this.configurationService.loadCodiceQuestionarioBotConfiguration(codiceBot), WorkerConst.CS_BOT_TYPE);
			Runnable runnable = null;
			for (BotWorker botWorker : this.botWorkers) {
				if (StringUtils.equalsIgnoreCase(botWorker.getType(), botType)) {
					runnable = botWorker.getRunnable(codiceBot);
					break;
				}
			}
			if (runnable != null) {
				thread = new Thread(runnable, codiceBot);
				thread.start();
				if (joinThread) {
					thread.join();
				}
				log.info("the thread [" + codiceBot + "] was lounched");
			} else {
				log.info("workerType [" + botType + "] of the bot [" + codiceBot + "] not found! Thread died.");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			log.info("ATTENZIONE! [" + codiceBot + "] è morto poiché: " + e.getMessage());
			log.info(getStackTrace(e));
		}
		return thread;
	}

	public Thread startNewThreadIsAliveController(String configPrefix, boolean joinThread, Map<String, Thread> threadList) {
		Thread thread = null;
		try {
			Runnable runnable = () -> {
				LocalDateTime fromDateTime = LocalDateTime.now();

				while (true) {
					try {
						LocalDateTime toDateTime = LocalDateTime.now();
						log.info("#################################################");
						for (String key : threadList.keySet()) {
							BotQstDTO botQstConfiguration = this.botQstService.findByCodiceBot(key);
							final String codiceQuestionario = botQstConfiguration.getCodiceQuestionario();
							String valore = this.configurationService.loadValueImmediately(codiceQuestionario, WorkerConst.CS_BOT_RELOAD_ON_FAIL);
							final Boolean isRestartOnFailEnabled = Boolean.parseBoolean(valore);
							log.info("Il bot " + key + " è abilitato al realod in caso di fallimento: [" + isRestartOnFailEnabled + "]");

							if (threadList.get(key) == null) {
								log.info("thread " + key + " " + EmojiParser.parseToUnicode(":red_circle:") + " INITIALIZATION FAILED");
							} else if (isRestartOnFailEnabled.booleanValue() && threadList.get(key) != null && !threadList.get(key).isAlive()) {
								log.info("thread " + key + " " + EmojiParser.parseToUnicode(":red_circle:") + " DIED");
								log.info("recovering it....");
								threadList.put(key, this.startNewThread(key, false));
								log.info("recovering of [" + key + "] completed :)");
							} else {
								log.info("thread " + key + " " + EmojiParser.parseToUnicode(":large_blue_circle:") + " is alive and is " + (threadList.get(key).isDaemon() ? "daemon" : "NOT daemon") + " thread");
							}
						}
						log.info("#################################################");
						log.info(EmojiParser.parseToUnicode(":alarm_clock:") + " life time: " + difference(toDateTime, fromDateTime));
						log.info("#################################################");
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						log.error("Errore bot SYSTEM. BOT SYSTEM DIED", e);
					}
				}
			};
			thread = new Thread(runnable, configPrefix);
			thread.start();
			if (joinThread) {
				thread.join();
			}
			log.info("the thread [" + configPrefix + "] was lounched");
		} catch (Throwable e) {
			e.printStackTrace();
			log.info(getStackTrace(e));
		}
		return thread;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.close();
		try {
			stringWriter.close();
		} catch (IOException e) {
		}
		return stringWriter.toString();
	}

	private String difference(LocalDateTime toDateTime, LocalDateTime fromDateTime) {
		LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

		long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears(years);

		long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths(months);

		long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays(days);

		long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours(hours);

		long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes(minutes);

		long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

		return years + " years " + months + " months " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds.";
	}

}
