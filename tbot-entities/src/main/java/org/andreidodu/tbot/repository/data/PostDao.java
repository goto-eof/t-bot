package org.andreidodu.tbot.repository.data;

import java.util.List;

import org.andreidodu.tbot.db.PostDB;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface PostDao extends CrudRepository<PostDB, Long>, QuerydslPredicateExecutor<PostDB> {

	List<PostDB> findAll();

	PostDB findByBotQst_CodiceBotAndBotQst_CodiceQuestionarioAndHash(String codiceBot, String codiceQuestionario, String hash);

	Long countByBotQst_CodiceBot(String codiceBot);

}
