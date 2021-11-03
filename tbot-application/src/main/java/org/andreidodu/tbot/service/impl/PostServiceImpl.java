package org.andreidodu.tbot.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.andreidodu.tbot.db.PostDB;
import org.andreidodu.tbot.db.QPostDB;
import org.andreidodu.tbot.dto.PostDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.mapper.form.PostMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.data.PostDao;
import org.andreidodu.tbot.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PostServiceImpl implements PostService {

	@Autowired
	private PostMapper mapper;

	@Autowired
	private PostDao repository;

	@Autowired
	private BotQstDao botQstRepository;

	@Override
	public void saveIfDoesNotExists(UrlInternalDTO urlInternal, List<PostDTO> posts) {
		List<PostDTO> results = this.calculateNotProcessedItems(urlInternal, posts);
		results.forEach(post -> {
			this.save(urlInternal, post);
		});

	}

	@Override
	public Long count(UrlInternalDTO utlInternal) {
		return this.repository.countByBotQst_CodiceBot(utlInternal.getCodiceBot());
	}

	@Override
	public List<PostDTO> calculateNotProcessedItems(UrlInternalDTO urlInternal, List<PostDTO> allRss) {
		List<PostDTO> result = new ArrayList<>();
		allRss.forEach(postDTOIn -> {
			log.debug("sto verificando se esiste il hash: [" + postDTOIn.getHash() + "]");

			QPostDB qPost = QPostDB.postDB;
			BooleanBuilder bb = new BooleanBuilder();

			bb.and(qPost.botQst.codiceBot.eq(urlInternal.getCodiceBot()));
			bb.and(qPost.botQst.codiceQuestionario.eq(urlInternal.getCodiceQuestionario()));

			BooleanBuilder bb2 = new BooleanBuilder();
			if (postDTOIn.getTitle() != null) {
				bb2.or(qPost.title.eq(postDTOIn.getTitle()));
			}
			if (postDTOIn.getDescription() != null) {
				bb2.or(qPost.description.eq(postDTOIn.getDescription()));
			}
			if (postDTOIn.getDate() != null) {
				bb2.or(qPost.date.eq(postDTOIn.getDate()));
			}
			if (postDTOIn.getLink() != null) {
				bb2.or(qPost.link.eq(postDTOIn.getLink()));
			}
			if (postDTOIn.getHash() != null) {
				bb2.or(qPost.hash.eq(postDTOIn.getHash()));
			}

			bb.and(bb2.getValue());
			Predicate predicate = bb.getValue();

			Iterable<PostDB> posts = this.repository.findAll(predicate);
			if (!posts.iterator().hasNext()) {
				log.debug("il hash NON esiste: [" + postDTOIn.getHash() + "]. Aggiunto l'oggetto post alla lista dei post non processati....");
				result.add(postDTOIn);
			}
		});

		return result;
	}

	@Override
	public PostDTO save(UrlInternalDTO urlInternal, PostDTO dto) {
		PostDB post = this.mapper.toDomain(dto);
		post.setBotQst(this.botQstRepository.findByCodiceBotAndCodiceQuestionario(urlInternal.getCodiceBot(), urlInternal.getCodiceQuestionario()));
		return this.mapper.toDTO(this.repository.save(post));

	}

}
