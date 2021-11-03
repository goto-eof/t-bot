package org.andreidodu.tbot.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "tf001_domande")
public class DomandaDB {

	@Id
	@Column(name = "tf001_c_dmd", nullable = false)
	private String id;

	@Column(name = "tf001_c_dmd_grp", length = 255, nullable = false)
	private String codiceGruppoDomanda;

	@Column(name = "tf001_c_fmt", length = 45, nullable = false)
	private String formato;

	@Column(name = "tf001_c_dom", length = 255, nullable = false)
	private String codiceDominio;

	@Column(name = "tf001_t_dmd", length = 255, nullable = false)
	private String testo;
	
	@Column(name = "tf001_t_sgr", length = 255, nullable = false)
	private String suggerimento;
	
	@Column(name = "tf001_c_type", length = 255, nullable = false)
	private String codiceTipologia;
	
	@Column(name = "insert_date", updatable = false, insertable = false)
	@JsonIgnore
	private Timestamp dateIns;

	@Column(name = "update_date", updatable = true, insertable = false)
	@JsonIgnore
	private Timestamp dateUpd;

	@Version
	@Column(name = "version")
	@JsonIgnore
	private Integer version;

}
