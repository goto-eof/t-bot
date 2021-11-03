package org.andreidodu.tbot.dto;

import lombok.Data;

@Data
public class QstTemplatesDTO {

	private Long id;
	private String codiceTemplate;
	private Integer ordine;
	private Boolean obbligatorio;
	private String codiceDomanda;

}
