package org.andreidodu.tbot.controllers;

import java.util.List;

import org.andreidodu.tbot.dto.UrlDTO;
import org.andreidodu.tbot.service.UrlConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/url-configuration")
public class UrlConfigurationController {

	@Autowired
	private UrlConfigurationService urlConfigurationService;

	@GetMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<List<UrlDTO>> getAll(@PathVariable(name = "codiceBot") String codiceBot) {
		return ResponseEntity.ok(this.urlConfigurationService.getAll(codiceBot));
	}

	@PostMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<UrlDTO> insert(@PathVariable(name = "codiceBot") String codiceBot) {
		return ResponseEntity.ok(this.urlConfigurationService.newUrl(codiceBot));
	}

	@DeleteMapping(path = "/codiceBot/{codiceBot}/codiceQuestionario/{codiceQuestionario}")
	public ResponseEntity<Boolean> delete(@PathVariable(name = "codiceBot") String codiceBot,
					@PathVariable(name = "codiceQuestionario") String codiceQuestionario) {
		return ResponseEntity.ok(this.urlConfigurationService.delete(codiceBot, codiceQuestionario));
	}

	@PutMapping(path = "/codiceBot/{codiceBot}")
	public ResponseEntity<UrlDTO> aggiorna(@PathVariable String codiceBot, @RequestBody UrlDTO urlDTO) {
		return ResponseEntity.ok(this.urlConfigurationService.update(codiceBot, urlDTO));
	}

	@PostMapping(path = "/reloadConfiguration/codiceBot/{codiceBot}/codiceQuestionario/{codiceQuestionario}/reload/{reload}")
	public ResponseEntity<Boolean> reloadConfiguration(@PathVariable(name = "codiceBot") String codiceBot,
					@PathVariable("codiceQuestionario") String codiceQuestionario, @PathVariable("reload") Boolean reload) {
		return ResponseEntity.ok(this.urlConfigurationService.reloadConfiguration(codiceBot, codiceQuestionario, reload));
	}

}
