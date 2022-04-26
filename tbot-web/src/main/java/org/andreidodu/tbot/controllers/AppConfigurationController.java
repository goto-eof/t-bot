package org.andreidodu.tbot.controllers;

import org.andreidodu.tbot.service.AppConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/app-configuration")
public class AppConfigurationController {

	private static final String BASE_URLS_PREFIX = "baseUrls";

	@Autowired
	private AppConfigurationService appConfigurationService;

	@GetMapping()
	public ResponseEntity<?> getConfiguration() {
		return ResponseEntity.ok(this.appConfigurationService.getConfigurationByPrefix());
	}

	@GetMapping(path = "/base-urls")
	public ResponseEntity<?> getUrls() {
		return ResponseEntity.ok(this.appConfigurationService.getConfigurationByPrefix(BASE_URLS_PREFIX));
	}

}
