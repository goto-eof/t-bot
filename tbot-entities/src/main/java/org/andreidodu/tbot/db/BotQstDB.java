package org.andreidodu.tbot.db;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "tf006_bot_qst", uniqueConstraints = { @UniqueConstraint(columnNames = { "tf006_c_bot", "tf003_c_qst" }) })
public class BotQstDB {

	@Id
	@Column(name = "tf006_bot_qst_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tf003_c_qst", length = 255, nullable = false)
	private String codiceQuestionario;

	@Column(name = "tf006_c_bot", nullable = false)
	private String codiceBot;

	@Column(name = "tf002_c_tmplt", nullable = false)
	private String codiceTemplate;

	@OneToMany(mappedBy = "botQst")
	private List<PostDB> posts;

	@OneToMany(mappedBy = "botQst")
	private List<BotQstStatusDB> botQstStatuses;
	
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
