package org.andreidodu.tbot.repository.config.form;

import java.util.List;

import org.andreidodu.tbot.db.QstTemplatesDB;
import org.springframework.data.repository.CrudRepository;

public interface QstTemplatesDao extends CrudRepository<QstTemplatesDB, Long> {

	List<QstTemplatesDB> findByCodiceTemplate(String codiceTemplate);

}
