package org.andreidodu.tbot.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "tf003_qst", uniqueConstraints = { @UniqueConstraint(columnNames = { "tf002_c_tmplt", "tf001_c_dmd", "tf003_c_qst" }) })
public class QuestionarioDB {

	@Id
	@Column(name = "tf003_qst_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tf003_c_qst", length = 255, nullable = false)
	private String codiceQuestionario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tf001_c_dmd", nullable = false)
	private DomandaDB domanda;

	@Column(name = "tf002_c_tmplt", length = 255, nullable = false)
	private String codiceTemplate;

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
