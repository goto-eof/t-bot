package org.andreidodu.tbot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDTO {
	private Long id;
	private Integer idxUrl;
	private String title;
	private String description;
	private String link;
	private String date;
	private String hash;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		PostDTO other = (PostDTO) obj;
		return this.getHash().equals(other.getHash());
		/*
		 * return getTitle()!=null && other.getTitle()!=null &&
		 * StringUtils.equalsIgnoreCase(getTitle(), other.getTitle()) ||
		 * getDescription()!=null && other.getDescription()!=null &&
		 * StringUtils.equalsIgnoreCase(getDescription(), other.getDescription()) ||
		 * getLink()!=null && other.getLink()!=null &&
		 * StringUtils.equalsIgnoreCase(getLink(), other.getLink()) || getDate()!=null
		 * && other.getDate()!=null && StringUtils.equalsIgnoreCase(getDate(),
		 * other.getDate());
		 */
	}
}
