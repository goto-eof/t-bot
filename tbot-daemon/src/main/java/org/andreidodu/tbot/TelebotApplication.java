package org.andreidodu.tbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.andreidodu.tbot.service.ConfigurationService;
import org.andreidodu.tbot.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TelebotApplication implements CommandLineRunner {

	private static ConfigurableApplicationContext run = null;

	@Autowired
	private ThreadUtil threadUtil;

	@Autowired
	private ConfigurationService configurationService;

	public static void main(String[] args) {
		run = SpringApplication.run(TelebotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Set<String> bots = this.configurationService.retrieveEnabledBots();

		Map<String, Thread> threads = new HashMap<>();
		bots.forEach(botName -> {
			Thread thread = this.threadUtil.startNewThread(botName, false);
			threads.put(botName, thread);
		});
		// check if threads are alive
		this.threadUtil.startNewThreadIsAliveController("system", false, threads);

	}

}
