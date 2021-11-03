package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class DomandaDTO {
	
	private String id;
	private String codiceGruppoDomanda;
	private String formato;
	private String codiceDominio;
	private String testo;
	private String suggerimento;
	private Boolean obbligatorio;
	private Integer order;
	
	
	private String valore;
	
	private List<DominioDTO> dominio = new ArrayList<>();
	
}
