package org.andreidodu.tbot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.constants.DomandaConst;
import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.DomandaDB;
import org.andreidodu.tbot.db.QuestionarioDB;
import org.andreidodu.tbot.db.RispostaDB;
import org.andreidodu.tbot.dto.BotDTO;
import org.andreidodu.tbot.dto.DomandaDTO;
import org.andreidodu.tbot.dto.RispostaDTO;
import org.andreidodu.tbot.mapper.form.DomandaMapper;
import org.andreidodu.tbot.mapper.form.QuestionarioTemplateMapper;
import org.andreidodu.tbot.mapper.form.RispostaMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.config.form.DomandaDao;
import org.andreidodu.tbot.repository.config.form.QstTemplatesDao;
import org.andreidodu.tbot.repository.config.form.QuestionarioDao;
import org.andreidodu.tbot.repository.config.form.RispostaDao;
import org.andreidodu.tbot.service.BotConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BotConfigurationServiceImpl implements BotConfigurationService {

	@Autowired
	private BotQstDao botQstDao;

	@Autowired
	private QuestionarioDao questionarioDao;

	@Autowired
	private DomandaDao domandaDao;

	@Autowired
	private DomandaMapper domandaMapper;

	@Autowired
	private RispostaDao rispostaDao;

	@Autowired
	private RispostaMapper rispostaMapper;

	@Autowired
	private QstTemplatesDao questionarioTemplateDao;

	@Autowired
	private QuestionarioTemplateMapper questionarioTemplateMapper;

	@Autowired
	private ConfigurationServiceCommonUil configurationServiceCommonUil;

	public List<BotDTO> getAllBots() {
		List<BotDTO> result = new ArrayList<>();

		List<BotQstDB> botQstsDB = this.botQstDao.findAll();
		botQstsDB.stream().filter(botQstDB -> ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG.equals(botQstDB.getCodiceTemplate())).forEach(botQstDB -> {

			String codiceQuestionario = botQstDB.getCodiceQuestionario();

			List<RispostaDB> risposteDB = this.configurationServiceCommonUil.caricaRisposte(codiceQuestionario);

			List<RispostaDTO> risposteDTO = this.rispostaMapper.toListDTO(risposteDB);

			this.configurationServiceCommonUil.associaValoreDaRispostaDBaRispostaDTO(risposteDB, risposteDTO);

			List<QuestionarioDB> questionariosDB = this.questionarioDao.findByCodiceQuestionario(codiceQuestionario);

			List<DomandaDB> domandeDB = this.configurationServiceCommonUil.extractDomandeDB(questionariosDB);

			List<DomandaDTO> caratteristiche = this.domandaMapper.toListDTO(domandeDB);

			caratteristiche = this.configurationServiceCommonUil.aggiungiDisegnoEOrdinaLista(caratteristiche,
							this.questionarioTemplateMapper.toListDTO(this.questionarioTemplateDao.findByCodiceTemplate(botQstDB.getCodiceTemplate())));

			this.configurationServiceCommonUil.caricaDomini(caratteristiche);

			this.associaRisposteDTOADomandeDTO(caratteristiche, risposteDTO);

			BotDTO bot = new BotDTO();
			bot.setCaratteristiche(caratteristiche);
			bot.setCodiceBot(botQstDB.getCodiceBot());
			result.add(bot);
		});

		botQstsDB.stream().filter(botQstDB -> ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG.equals(botQstDB.getCodiceTemplate())).forEach(botQstDB -> {
			// @formatter: off
			BotDTO botDTO = result.stream().filter(bott -> botQstDB.getCodiceBot().equals(bott.getCodiceBot())).findFirst().get();
			// @formatter: on
			botDTO.getCodiciUrl().add(botQstDB.getCodiceQuestionario());

		});

		return result;
	}

	@Override
	public BotDTO newBot() {
		BotDTO bot = new BotDTO();

		String id = String.valueOf(Calendar.getInstance().getTimeInMillis());
		String codiceBot = "BOT-" + id;
		String codiceQuestionario = "QST-" + id;
		// tc006_bot_qst
		BotQstDB botQstDB = this.configurationServiceCommonUil.createAndPersistBotQstDB(codiceBot, codiceQuestionario,
						ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG);
		// tf003_qst
		List<QuestionarioDB> questionariosDB = this.configurationServiceCommonUil.popolaTabellaQst(ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG,
						codiceQuestionario);
		// extract tf001_domande
		List<DomandaDB> domandeDB = this.configurationServiceCommonUil.extractDomandeDB(questionariosDB);

		this.addCaratteristicheDiDefault(codiceBot, codiceQuestionario, questionariosDB);

		List<DomandaDTO> caratteristiche = this.domandaMapper.toListDTO(domandeDB);

		List<RispostaDB> risposteDB = this.configurationServiceCommonUil.caricaRisposte(codiceQuestionario);

		List<RispostaDTO> risposte = this.rispostaMapper.toListDTO(risposteDB);

		this.configurationServiceCommonUil.associaValoreDaRispostaDBaRispostaDTO(risposteDB, risposte);

		this.configurationServiceCommonUil.associaRisposteDTOADomandeDTO(caratteristiche, risposte);

		this.configurationServiceCommonUil.caricaDomini(caratteristiche);

		bot.setCaratteristiche(caratteristiche);

		bot.setCodiceBot(codiceBot);

		return bot;
	}

	private void associaRisposteDTOADomandeDTO(List<DomandaDTO> caratteristiche, List<RispostaDTO> risposte) {
		caratteristiche.forEach(domanda -> {
			risposte.forEach(risposta -> {
				if (domanda.getId().equals(risposta.getCodiceDomanda())) {
					domanda.setValore(risposta.getValore());
				}
			});
		});

	}

	private void addCaratteristicheDiDefault(String codiceBot, String codiceQuestionario, List<QuestionarioDB> questionariosDB) {

		questionariosDB.forEach(questionarioDB -> {
			if ("D030".equals(questionarioDB.getDomanda().getId())) {
				RispostaDB risposta = new RispostaDB();
				risposta.setCodiceQuestionario(codiceQuestionario);
				risposta.setDomanda(questionarioDB.getDomanda());
				risposta.setValoreStringa(codiceBot);
				risposta = this.rispostaDao.save(risposta);
			} else if ("D040".equals(questionarioDB.getDomanda().getId())) {
				RispostaDB risposta = new RispostaDB();
				risposta.setCodiceQuestionario(codiceQuestionario);
				risposta.setDomanda(questionarioDB.getDomanda());
				risposta.setValoreStringa("Il mio primo Bot");
				risposta = this.rispostaDao.save(risposta);
			}
		});

	}

	@Override
	public Boolean delete(String codiceBot) {
		this.botQstDao.deleteByCodiceBot(codiceBot);
		return Boolean.TRUE;
	}

	@Override
	public BotDTO update(String codiceBot, BotDTO botDTO) {
		List<BotQstDB> questioanriBots = this.botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG);

		questioanriBots.forEach(qb -> {
			String codiceQuestionario = qb.getCodiceQuestionario();
			List<QuestionarioDB> questionariosDB = this.questionarioDao.findByCodiceQuestionario(codiceQuestionario);

			questionariosDB.stream().filter(questionarioDB -> DomandaConst.CodiceTipologia.ORDINARIO.equals(questionarioDB.getDomanda().getCodiceTipologia()))
							.forEach(q -> {
								Optional<DomandaDTO> domandaOpt = botDTO.getCaratteristiche().stream()
												.filter(item -> item.getId().equals(q.getDomanda().getId())).findFirst();

								DomandaDTO domanda = domandaOpt.get();
								String codiceDomanda = q.getDomanda().getId();
								RispostaDB rispostaDB = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, codiceDomanda);
								if (rispostaDB != null) {
									String formato = domanda.getFormato();
									if (Arrays.asList("STR", "BOL", "DOM").contains(formato)) {
										rispostaDB.setValoreStringa(domanda.getValore());
									} else if ("TXT".equals(formato)) {
										rispostaDB.setValoreTestuale(domanda.getValore());
									} else if ("NUM".equals(formato)) {
										rispostaDB.setValoreNumerico(domanda.getValore() == null ? null : Long.parseLong(domanda.getValore()));
									}
									this.rispostaDao.save(rispostaDB);
								} else {
									rispostaDB = new RispostaDB();
									rispostaDB.setCodiceQuestionario(codiceQuestionario);
									rispostaDB.setDomanda(q.getDomanda());
									String formato = domanda.getFormato();
									if (Arrays.asList("STR", "BOL", "DOM").contains(formato)) {
										rispostaDB.setValoreStringa(domanda.getValore());
									} else if ("TXT".equals(formato)) {
										rispostaDB.setValoreTestuale(domanda.getValore());
									} else if ("NUM".equals(formato)) {
										rispostaDB.setValoreNumerico(domanda.getValore() == null ? null : Long.parseLong(domanda.getValore()));
									}
									this.rispostaDao.save(rispostaDB);
								}
							});
		});

		return botDTO;
	}

	@Override
	public Boolean reloadConfiguration(String codiceBot, Boolean reload) {
		final String valore = reload.booleanValue() ? "DA_AGGIORNARE" : "AGGIORNATO";
		final String codiceQuestionario = this.botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG)
						.get(0).getCodiceQuestionario();
		RispostaDB risposta = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, WorkerConst.RELOAD_BOT_CONFIGURATION);
		if (risposta == null) {
			risposta = new RispostaDB();
			risposta.setCodiceQuestionario(codiceQuestionario);
			risposta.setValoreStringa(valore);
			risposta.setDomanda(this.domandaDao.findById(WorkerConst.RELOAD_BOT_CONFIGURATION).get());
			this.rispostaDao.save(risposta);
		} else {
			risposta.setValoreStringa(valore);
			this.rispostaDao.save(risposta);
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean reloadAllConfiguration(String codiceBot, Boolean reload) {
		final String valore = reload.booleanValue() ? "DA_AGGIORNARE" : "AGGIORNATO";
		final String codiceQuestionario = this.botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_BOT_CONFIG)
						.get(0).getCodiceQuestionario();
		RispostaDB risposta = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, WorkerConst.RELOAD_ALL_CONFIGURATION);
		if (risposta == null) {
			risposta = new RispostaDB();
			risposta.setCodiceQuestionario(codiceQuestionario);
			risposta.setValoreStringa(valore);
			risposta.setDomanda(this.domandaDao.findById(WorkerConst.RELOAD_ALL_CONFIGURATION).get());
			this.rispostaDao.save(risposta);
		} else {
			risposta.setValoreStringa(valore);
			this.rispostaDao.save(risposta);
		}
		return Boolean.TRUE;
	}

}
