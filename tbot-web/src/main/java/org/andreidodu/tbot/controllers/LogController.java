package org.andreidodu.tbot.controllers;

import java.util.List;

import org.andreidodu.tbot.dto.LogRowDTO;
import org.andreidodu.tbot.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(path = "/log")
public class LogController {

	@Autowired
	private LogService logService;

	@GetMapping
	public ResponseEntity<List<LogRowDTO>> getAll() {
		return ResponseEntity.ok(this.logService.retrieveLog());
	}

	@GetMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<List<LogRowDTO>> getByCodiceBot(@PathVariable(name = "codiceBot") String codiceBot) {
		return ResponseEntity.ok(this.logService.retrieveLog(codiceBot));
	}

}
