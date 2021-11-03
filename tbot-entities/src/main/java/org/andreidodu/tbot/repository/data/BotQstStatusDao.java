package org.andreidodu.tbot.repository.data;

import java.util.List;

import org.andreidodu.tbot.db.BotQstStatusDB;
import org.springframework.data.repository.CrudRepository;

public interface BotQstStatusDao extends CrudRepository<BotQstStatusDB, Long> {

	List<BotQstStatusDB> findAll();

	BotQstStatusDB findByBotQst_CodiceBotAndBotQst_CodiceQuestionarioAndKey(String codiceBot, String codiceQuestionario, String key);

}
