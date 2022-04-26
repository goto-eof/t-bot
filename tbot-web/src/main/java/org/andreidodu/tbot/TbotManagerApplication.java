	package org.andreidodu.tbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class TbotManagerApplication {

	@SuppressWarnings("unused")
	private static ConfigurableApplicationContext run = null;

	public static void main(String[] args) {
		run = SpringApplication.run(TbotManagerApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> defaultCorsFilter() {
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(Boolean.FALSE);
		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		corsConfigurationSource.registerCorsConfiguration("/**", configuration);
		return new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
	}

}
