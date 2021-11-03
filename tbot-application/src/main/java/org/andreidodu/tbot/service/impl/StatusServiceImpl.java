package org.andreidodu.tbot.service.impl;

import javax.transaction.Transactional;

import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.BotQstStatusDB;
import org.andreidodu.tbot.dto.BotQstStatusDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.mapper.form.BotQstStatusMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.data.BotQstStatusDao;
import org.andreidodu.tbot.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StatusServiceImpl implements StatusService {

	@Autowired
	private BotQstStatusDao repository;

	@Autowired
	private BotQstStatusMapper mapper;

	@Autowired
	private BotQstDao botQstRepository;

	@Override
	public BotQstStatusDTO insertOrUpdate(UrlInternalDTO urlInternal, String key, String value) {
		BotQstStatusDB status = this.repository.findByBotQst_CodiceBotAndBotQst_CodiceQuestionarioAndKey(urlInternal.getCodiceBot(),
						urlInternal.getCodiceQuestionario(), key);
		if (status == null) {
			BotQstDB botQst = this.botQstRepository.findByCodiceBotAndCodiceQuestionario(urlInternal.getCodiceBot(), urlInternal.getCodiceQuestionario());
			status = new BotQstStatusDB();
			status.setBotQst(botQst);
			status.setKey(key);
			status.setValue(value);
			this.repository.save(status);
		} else {
			status.setValue(value);
			this.repository.save(status);
		}
		return this.mapper.toDTO(status);
	}

	@Override
	public String get(UrlInternalDTO urlInternal, String key) {
		BotQstStatusDB status = this.repository.findByBotQst_CodiceBotAndBotQst_CodiceQuestionarioAndKey(urlInternal.getCodiceBot(),
						urlInternal.getCodiceQuestionario(), key);
		return status == null ? null : status.getValue();
	}

}
