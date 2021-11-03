package org.andreidodu.tbot.service.impl;

import javax.transaction.Transactional;

import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.dto.BotQstDTO;
import org.andreidodu.tbot.mapper.form.BotQstMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.service.BotQstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BotQstServiceImpl implements BotQstService {

	@Autowired
	private BotQstDao botQstDao;

	@Autowired
	private BotQstMapper botQstMapper;

	@Override
	public BotQstDTO findByCodiceBot(String codiceBot) {
		BotQstDB botQstConfiguration = this.botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG).get(0);
		return this.botQstMapper.toDTO(botQstConfiguration);
	}

}
