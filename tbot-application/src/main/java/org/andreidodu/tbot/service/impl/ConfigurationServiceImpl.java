package org.andreidodu.tbot.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.RispostaDB;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.config.form.DomandaDao;
import org.andreidodu.tbot.repository.config.form.RispostaDao;
import org.andreidodu.tbot.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private RispostaDao rispostaDao;

	@Autowired
	private DomandaDao domandaDao;

	@Autowired
	private BotQstDao botQstDao;

	@Autowired
	private ConfigurationServiceCommonUil configurationServiceCommonUil;

	@Override
	public ConfigurationDTO loadConfiguration(String codiceBot) {
		ConfigurationDTO result = new ConfigurationDTO();

		result.setCodiceBot(codiceBot);

		final String codiceQuestionarioBot = this.loadCodiceQuestionarioBotConfiguration(codiceBot);
		result.setCodiceQuestionarioBot(codiceQuestionarioBot);

		Map<String, String> botConfigurationMap = loadBotConfigurationMap(codiceQuestionarioBot);
		result.setBotConfiguration(botConfigurationMap);

		Map<String, Map<String, String>> urlsConfigurationMap = loadUrlsConfigurationMap(codiceBot);
		result.setUrlsConfiguration(urlsConfigurationMap);

		return result;
	}

	@Override
	public ConfigurationDTO loadConfiguration(ConfigurationDTO configuration, String codiceQuestionario) {
		configuration.getUrlsConfiguration().put(codiceQuestionario, this.loadRisposteByCodiceQuestionario(codiceQuestionario));
		return configuration;
	}

	private Map<String, Map<String, String>> loadUrlsConfigurationMap(String botName) {
		Map<String, Map<String, String>> urlsConfigurationMap = new HashMap<>();
		List<BotQstDB> botQstUrls = this.botQstDao.findByCodiceBotAndCodiceTemplate(botName, ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG);

		botQstUrls.forEach(botQstUrl -> {
			final String codiceQuestionario = botQstUrl.getCodiceQuestionario();

			Map<String, String> currentRisposteMap = loadRisposteByCodiceQuestionario(codiceQuestionario);

			urlsConfigurationMap.put(codiceQuestionario, currentRisposteMap);

		});
		return urlsConfigurationMap;
	}

	@Override
	public Map<String, String> loadRisposteByCodiceQuestionario(final String codiceQuestionario) {
		Map<String, String> currentRisposteMap = new HashMap<>();
		List<RispostaDB> risposteUrl = this.rispostaDao.findByCodiceQuestionario(codiceQuestionario);
		risposteUrl.forEach(risposta -> {
			currentRisposteMap.put(risposta.getDomanda().getId(), configurationServiceCommonUil.calculateValore(risposta.getDomanda().getFormato(), risposta));
		});
		return currentRisposteMap;
	}

	@Override
	public String loadCodiceQuestionarioBotConfiguration(String codiceBot) {
		BotQstDB botQstConfiguration = this.botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG).get(0);
		final String codiceQuestionario = botQstConfiguration.getCodiceQuestionario();

		return codiceQuestionario;
	}

	@Override
	public Map<String, String> loadBotConfigurationMap(String codiceQuestionario) {

		Map<String, String> botConfigurationMap = loadRisposteByCodiceQuestionario(codiceQuestionario);

		return botConfigurationMap;
	}

	@Override
	public Set<String> retrieveEnabledBots() {
		List<BotQstDB> botQsts = this.botQstDao.findAll();
		Set<String> result = new HashSet<>();
		botQsts.forEach(botQst -> {
			result.add(botQst.getCodiceBot());
		});
		return result;
	}

	@Override
	public String loadValueImmediately(String codiceQuestionario, String codiceDomanda) {
		// TODO da ottimizzare
		return loadRisposteByCodiceQuestionario(codiceQuestionario).get(codiceDomanda);
	}

	@Override
	public void aggiornaStatoCaricamentoConfigurazione(String codiceQuestionario, String codiceDomanda, String value) {
		RispostaDB risposta = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, codiceDomanda);

		if (risposta == null) {
			risposta = new RispostaDB();
			risposta.setCodiceQuestionario(codiceQuestionario);
			risposta.setDomanda(this.domandaDao.findById(codiceDomanda).get());
			risposta.setValoreStringa(value);
			this.rispostaDao.save(risposta);
		} else {
			risposta.setValoreStringa(value);
			this.rispostaDao.save(risposta);
		}
	}

}
