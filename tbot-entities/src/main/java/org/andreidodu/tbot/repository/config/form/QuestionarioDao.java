package org.andreidodu.tbot.repository.config.form;

import java.util.List;

import org.andreidodu.tbot.db.QuestionarioDB;
import org.springframework.data.repository.CrudRepository;

public interface QuestionarioDao extends CrudRepository<QuestionarioDB, Long> {

	List<QuestionarioDB> findAll();

	List<QuestionarioDB> findByCodiceQuestionario(String codiceQuestionario);

	List<QuestionarioDB> findByCodiceQuestionarioAndCodiceTemplate(String codiceQuestionario, String codiceTemplate);

	void deleteByCodiceQuestionario(String codiceQuestionario);

}
