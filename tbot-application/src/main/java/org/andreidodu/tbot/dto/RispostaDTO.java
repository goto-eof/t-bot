package org.andreidodu.tbot.dto;

import lombok.Data;

@Data
public class RispostaDTO{
	private Long id;
	private String codiceQuestionario;
	private String codiceDomanda;
	private String valore;
}
