package org.andreidodu.tbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TbotManagerApplication {

	@SuppressWarnings("unused")
	private static ConfigurableApplicationContext run = null;

	public static void main(String[] args) {
		run = SpringApplication.run(TbotManagerApplication.class, args);
	}

}
