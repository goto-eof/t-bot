package org.andreidodu.tbot.db;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DominioPK implements Serializable {

	private static final long serialVersionUID = 1589046526779802397L;

	private String codiceDominio;
	private String valoreDominio;

}
