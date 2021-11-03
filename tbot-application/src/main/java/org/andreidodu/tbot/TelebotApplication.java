package org.andreidodu.tbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = { "application.yml" })
public class TelebotApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TelebotApplication.class, args).close();
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
