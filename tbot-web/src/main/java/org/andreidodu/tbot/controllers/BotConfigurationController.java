package org.andreidodu.tbot.controllers;

import java.util.List;

import org.andreidodu.tbot.dto.BotDTO;
import org.andreidodu.tbot.service.BotConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(path = "/bot-configuration")
public class BotConfigurationController {

	@Autowired
	private BotConfigurationService botConfigurationService;

	@GetMapping()
	public ResponseEntity<List<BotDTO>> getAll() {
		return ResponseEntity.ok(this.botConfigurationService.getAllBots());
	}

	@PostMapping()
	public ResponseEntity<BotDTO> createBot() {
		return ResponseEntity.ok(this.botConfigurationService.newBot());
	}

	@DeleteMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<Boolean> delete(@PathVariable String codiceBot) {
		return ResponseEntity.ok(this.botConfigurationService.delete(codiceBot));
	}

	@PutMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<BotDTO> aggiorna(@PathVariable String codiceBot, @RequestBody BotDTO botDTO) {
		return ResponseEntity.ok(this.botConfigurationService.update(codiceBot, botDTO));
	}

	@PostMapping(path = "/reloadConfiguration/codiceBot/{codiceBot}/reload/{reload}")
	public ResponseEntity<Boolean> reloadConfiguration(@PathVariable("codiceBot") String codiceBot, @PathVariable("reload") Boolean reload) {
		return ResponseEntity.ok(this.botConfigurationService.reloadConfiguration(codiceBot, reload));
	}

	@PostMapping(path = "/reloadAllConfiguration/codiceBot/{codiceBot}/reload/{reload}")
	public ResponseEntity<Boolean> reloadAllConfiguration(@PathVariable("codiceBot") String codiceBot, @PathVariable("reload") Boolean reload) {
		return ResponseEntity.ok(this.botConfigurationService.reloadAllConfiguration(codiceBot, reload));
	}

}
