package org.andreidodu.tbot.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "tc001_post")
public class PostDB {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tc001_post_seq", nullable = false)
	private Long id;

	@Column(name = "tc001_title", length = 45, nullable = false)
	private String title;

	@Column(name = "tc001_description", length = 45, nullable = false)
	private String description;

	@Column(name = "tc001_link", length = 45, nullable = false)
	private String link;

	@Column(name = "tc001_date", length = 45, nullable = false)
	private String date;

	@Column(name = "tc001_hash", length = 45, nullable = false)
	private String hash;

	@ManyToOne
	@JoinColumn(name = "tf006_bot_qst_seq", nullable = false)
	private BotQstDB botQst;

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
