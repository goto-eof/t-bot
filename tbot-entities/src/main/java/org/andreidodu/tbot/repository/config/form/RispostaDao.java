package org.andreidodu.tbot.repository.config.form;

import java.util.List;

import org.andreidodu.tbot.db.RispostaDB;
import org.springframework.data.repository.CrudRepository;

public interface RispostaDao extends CrudRepository<RispostaDB, Long> {

	List<RispostaDB> findByCodiceQuestionario(String codiceQuestionario);

	RispostaDB findByCodiceQuestionarioAndDomanda_Id(String codiceQuestionario, String codiceDomanda);

}
