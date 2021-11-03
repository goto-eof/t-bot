package org.andreidodu.tbot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.constants.DomandaConst;
import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.DomandaDB;
import org.andreidodu.tbot.db.QuestionarioDB;
import org.andreidodu.tbot.db.RispostaDB;
import org.andreidodu.tbot.dto.DomandaDTO;
import org.andreidodu.tbot.dto.RispostaDTO;
import org.andreidodu.tbot.dto.UrlDTO;
import org.andreidodu.tbot.mapper.form.DomandaMapper;
import org.andreidodu.tbot.mapper.form.QuestionarioTemplateMapper;
import org.andreidodu.tbot.mapper.form.RispostaMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.config.form.DomandaDao;
import org.andreidodu.tbot.repository.config.form.QstTemplatesDao;
import org.andreidodu.tbot.repository.config.form.QuestionarioDao;
import org.andreidodu.tbot.repository.config.form.RispostaDao;
import org.andreidodu.tbot.service.UrlConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UrlConfigurationServiceImpl implements UrlConfigurationService {

	@Autowired
	private ConfigurationServiceCommonUil configurationServiceCommonUil;

	@Autowired
	private DomandaMapper domandaMapper;

	@Autowired
	private RispostaMapper rispostaMapper;

	@Autowired
	private QuestionarioDao questionarioDao;

	@Autowired
	private QstTemplatesDao questionarioTemplateDao;

	@Autowired
	private QuestionarioTemplateMapper questionarioTemplateMapper;

	@Autowired
	private BotQstDao botQstDao;

	@Autowired
	private RispostaDao rispostaDao;

	@Autowired
	private DomandaDao domandaDao;

	@Override
	public List<UrlDTO> getAll(String codiceBot) {
		List<UrlDTO> result = new ArrayList<>();
		botQstDao.findByCodiceBotAndCodiceTemplate(codiceBot, ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG).stream().forEach(botQstDB -> {

			String codiceQuestionario = botQstDB.getCodiceQuestionario();

			List<QuestionarioDB> questionariosDB = this.questionarioDao.findByCodiceQuestionario(codiceQuestionario);

			List<DomandaDTO> caratteristiche = recuperaDisegnoERisposte(codiceQuestionario, botQstDB, questionariosDB);

			UrlDTO urlDTO = new UrlDTO();
			urlDTO.setCaratteristiche(caratteristiche);
			urlDTO.setCodiceQuestionario(codiceQuestionario);
			result.add(urlDTO);
		});
		return result;
	}

	public UrlDTO newUrl(String codiceBot) {
		UrlDTO result = new UrlDTO();

		String id = String.valueOf(Calendar.getInstance().getTimeInMillis());
		String codiceQuestionario = "QST-" + id;

		// tc006_bot_qst
		BotQstDB botQstDB = this.configurationServiceCommonUil.createAndPersistBotQstDB(codiceBot, codiceQuestionario,
						ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG);
		// tf003_qst
		List<QuestionarioDB> questionariosDB = this.configurationServiceCommonUil.popolaTabellaQst(ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG,
						codiceQuestionario);

		List<DomandaDTO> caratteristiche = recuperaDisegnoERisposte(codiceQuestionario, botQstDB, questionariosDB);

		result.setCaratteristiche(caratteristiche);

		result.setCodiceQuestionario(codiceQuestionario);

		return result;
	}

	private List<DomandaDTO> recuperaDisegnoERisposte(String codiceQuestionario, BotQstDB botQstDB, List<QuestionarioDB> questionariosDB) {

		List<DomandaDB> domandeDB = this.configurationServiceCommonUil.extractDomandeDB(questionariosDB);

		List<DomandaDTO> caratteristiche = this.domandaMapper.toListDTO(domandeDB);

		caratteristiche = this.configurationServiceCommonUil.aggiungiDisegnoEOrdinaLista(caratteristiche,
						this.questionarioTemplateMapper.toListDTO(this.questionarioTemplateDao.findByCodiceTemplate(botQstDB.getCodiceTemplate())));

		List<RispostaDB> risposteDB = this.configurationServiceCommonUil.caricaRisposte(codiceQuestionario);

		List<RispostaDTO> risposte = this.rispostaMapper.toListDTO(risposteDB);

		this.configurationServiceCommonUil.associaValoreDaRispostaDBaRispostaDTO(risposteDB, risposte);

		this.configurationServiceCommonUil.associaRisposteDTOADomandeDTO(caratteristiche, risposte);

		this.configurationServiceCommonUil.caricaDomini(caratteristiche);
		return caratteristiche;
	}

	@Override
	public Boolean delete(String codiceBot, String codiceQuestionario) {
		this.questionarioDao.deleteByCodiceQuestionario(codiceQuestionario);
		this.botQstDao.deleteByCodiceBotAndCodiceQuestionario(codiceBot, codiceQuestionario);
		return Boolean.TRUE;
	}

	@Override
	public UrlDTO update(String codiceBot, UrlDTO urlDTO) {
		String codiceQuestionario = urlDTO.getCodiceQuestionario();
		this.questionarioDao.findByCodiceQuestionarioAndCodiceTemplate(codiceQuestionario, ConfigurationServiceCommonUil.CD_TEMPLATE_URL_CONFIG).stream()
						.filter(questionarioDB -> DomandaConst.CodiceTipologia.ORDINARIO.equals(questionarioDB.getDomanda().getCodiceTipologia()))
						.forEach(questionarioDB -> {
							String codiceDomanda = questionarioDB.getDomanda().getId();
							DomandaDTO domandaDTO = urlDTO.getCaratteristiche().stream().filter(d -> codiceDomanda.equals(d.getId())).findFirst().get();

							RispostaDB rispostaDB = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, codiceDomanda);

							if (rispostaDB == null) {
								rispostaDB = new RispostaDB();
								rispostaDB.setCodiceQuestionario(codiceQuestionario);
								rispostaDB.setDomanda(questionarioDB.getDomanda());
								String formato = domandaDTO.getFormato();
								if (Arrays.asList("STR", "BOL", "DOM").contains(formato)) {
									rispostaDB.setValoreStringa(domandaDTO.getValore());
								} else if ("TXT".equals(formato)) {
									rispostaDB.setValoreTestuale(domandaDTO.getValore());
								} else if ("NUM".equals(formato)) {
									rispostaDB.setValoreNumerico(domandaDTO.getValore() == null ? null : Long.parseLong(domandaDTO.getValore()));
								}
							} else {
								String formato = domandaDTO.getFormato();
								if (Arrays.asList("STR", "BOL", "DOM").contains(formato)) {
									rispostaDB.setValoreStringa(domandaDTO.getValore());
								} else if ("TXT".equals(formato)) {
									rispostaDB.setValoreTestuale(domandaDTO.getValore());
								} else if ("NUM".equals(formato)) {
									rispostaDB.setValoreNumerico(domandaDTO.getValore() == null ? null : Long.parseLong(domandaDTO.getValore()));
								}
							}
							this.rispostaDao.save(rispostaDB);
						});

		return urlDTO;
	}

	@Override
	public Boolean reloadConfiguration(String codiceBot, String codiceQuestionario, Boolean reload) {
		final String valore = reload.booleanValue() ? "DA_AGGIORNARE" : "AGGIORNATO";
		RispostaDB risposta = this.rispostaDao.findByCodiceQuestionarioAndDomanda_Id(codiceQuestionario, WorkerConst.RELOAD_URL_CONFIGURATION);
		if (risposta == null) {
			risposta = new RispostaDB();
			risposta.setCodiceQuestionario(codiceQuestionario);
			risposta.setValoreStringa(valore);
			risposta.setDomanda(this.domandaDao.findById(WorkerConst.RELOAD_URL_CONFIGURATION).get());
			this.rispostaDao.save(risposta);
		} else {
			risposta.setValoreStringa(valore);
			this.rispostaDao.save(risposta);
		}
		return Boolean.TRUE;
	}

}
