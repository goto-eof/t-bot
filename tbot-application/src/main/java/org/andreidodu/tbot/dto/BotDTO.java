package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BotDTO {

	private String codiceBot;
	private List<DomandaDTO> caratteristiche = new ArrayList<>();
	private List<UrlDTO> urls = new ArrayList<>();
	private List<String> codiciUrl = new ArrayList<>();
	
}
