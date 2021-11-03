package org.andreidodu.tbot.repository.config.form;

import java.util.List;

import org.andreidodu.tbot.db.DominioDB;
import org.springframework.data.repository.CrudRepository;

public interface DominioDao extends CrudRepository<DominioDB, Long> {

	List<DominioDB> findByCodiceDominio(String codiceDominio);

	DominioDB findByCodiceDominioAndValoreDominio(String codiceDominio, String codiceValore);
}
