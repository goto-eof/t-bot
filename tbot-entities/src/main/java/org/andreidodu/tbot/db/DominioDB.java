package org.andreidodu.tbot.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "tf005_domini", uniqueConstraints = { @UniqueConstraint(columnNames = { "tf005_c_dmn", "tf005_c_dmn_val" }) })
@IdClass(DominioPK.class)
public class DominioDB implements Serializable {

	private static final long serialVersionUID = -128531959101068749L;

	@Id
	@Column(name = "tf005_c_dmn", length = 255, nullable = false)
	private String codiceDominio;

	@Id
	@Column(name = "tf005_c_dmn_val", length = 255, nullable = false)
	private String valoreDominio;

	@Column(name = "tf005_c_dmn_des", length = 255, nullable = false)
	private String descrizione;

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
