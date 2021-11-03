package org.andreidodu.tbot.repository.config.form;

import java.util.List;

import org.andreidodu.tbot.db.BotQstDB;
import org.springframework.data.repository.CrudRepository;

public interface BotQstDao extends CrudRepository<BotQstDB, Long> {

	List<BotQstDB> findAll();

	void deleteByCodiceBot(String codiceBot);

	List<BotQstDB> findByCodiceBot(String codiceBot);

	List<BotQstDB> findByCodiceBotAndCodiceTemplate(String codiceBot, String codiceTemplate);

	void deleteByCodiceBotAndCodiceQuestionario(String codiceBot, String codiceQuestionario);

	BotQstDB findByCodiceBotAndCodiceQuestionario(String codiceBot, String codiceQuestionario);

}
