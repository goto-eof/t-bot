package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UrlDTO {

	private String codiceQuestionario;
	private List<DomandaDTO> caratteristiche = new ArrayList<>();

}
