package org.andreidodu.tbot.dto;

import lombok.Data;

@Data
public class QuestionarioDTO {

	private Long id;
	private String codiceQuestionario;
	private DomandaDTO domanda;
	private String codiceTemplate;
	private String tipologia;

}
