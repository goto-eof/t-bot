package org.andreidodu.tbot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.andreidodu.tbot.constants.DomandaConst;
import org.andreidodu.tbot.db.BotQstDB;
import org.andreidodu.tbot.db.DomandaDB;
import org.andreidodu.tbot.db.QuestionarioDB;
import org.andreidodu.tbot.db.RispostaDB;
import org.andreidodu.tbot.dto.DomandaDTO;
import org.andreidodu.tbot.dto.QstTemplatesDTO;
import org.andreidodu.tbot.dto.RispostaDTO;
import org.andreidodu.tbot.mapper.form.DominioMapper;
import org.andreidodu.tbot.repository.config.form.BotQstDao;
import org.andreidodu.tbot.repository.config.form.DominioDao;
import org.andreidodu.tbot.repository.config.form.QstTemplatesDao;
import org.andreidodu.tbot.repository.config.form.QuestionarioDao;
import org.andreidodu.tbot.repository.config.form.RispostaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationServiceCommonUil {

	public final static String CD_TEMPLATE_BOT_CONFIG = "bot_configuration";
	public final static String CD_TEMPLATE_URL_CONFIG = "url_configuration";

	@Autowired
	private QstTemplatesDao qstTemplatesDao;

	@Autowired
	private QuestionarioDao questionarioDao;

	@Autowired
	private BotQstDao botQstDao;

	@Autowired
	private RispostaDao rispostaDao;

	@Autowired
	private DominioMapper dominioMapper;

	@Autowired
	private DominioDao dominioDao;

	public BotQstDB createAndPersistBotQstDB(String codiceBot, String codiceQuestionario, String codiceTemplate) {
		BotQstDB botQstDB = new BotQstDB();
		botQstDB.setCodiceBot(codiceBot);
		botQstDB.setCodiceQuestionario(codiceQuestionario);
		botQstDB.setCodiceTemplate(codiceTemplate);
		botQstDB = this.botQstDao.save(botQstDB);
		return botQstDB;
	}

	public List<QuestionarioDB> popolaTabellaQst(String codiceTemplate, String codiceQuestionario) {
		List<QuestionarioDB> result = new ArrayList<>();
		this.qstTemplatesDao.findByCodiceTemplate(codiceTemplate).forEach(questionarioTemplateDB -> {
			QuestionarioDB questionarioDB = new QuestionarioDB();
			questionarioDB.setDomanda(questionarioTemplateDB.getDomanda());
			questionarioDB.setCodiceTemplate(questionarioTemplateDB.getCodiceTemplate());
			questionarioDB.setCodiceQuestionario(codiceQuestionario);
			questionarioDB = this.questionarioDao.save(questionarioDB);
			result.add(questionarioDB);
		});
		return result;
	}

	public List<DomandaDB> extractDomandeDB(List<QuestionarioDB> questionariosDB) {
		return questionariosDB.stream()
						.filter(questionarioDB -> DomandaConst.CodiceTipologia.ORDINARIO.equals(questionarioDB.getDomanda().getCodiceTipologia()))
						.map(questionarioDB -> {
							return questionarioDB.getDomanda();
						}).collect(Collectors.toList());
	}

	public List<DomandaDB> extractAllDomandeDB(List<QuestionarioDB> questionariosDB) {
		return questionariosDB.stream().map(questionarioDB -> {
			return questionarioDB.getDomanda();
		}).collect(Collectors.toList());
	}

	public List<RispostaDB> caricaRisposte(String codiceQuestionario) {
		return this.rispostaDao.findByCodiceQuestionario(codiceQuestionario);
	}

	public void associaValoreDaRispostaDBaRispostaDTO(List<RispostaDB> risposteDB, List<RispostaDTO> risposte) {
		risposteDB.forEach(rispostaDB -> {
			risposte.forEach(risposta -> {
				if (risposta.getCodiceDomanda().equals(rispostaDB.getDomanda().getId())) {
					risposta.setValore(this.calculateValore(rispostaDB.getDomanda().getFormato(), rispostaDB));
				}
			});
		});
	}

	public void associaRisposteDTOADomandeDTO(List<DomandaDTO> caratteristiche, List<RispostaDTO> risposte) {
		caratteristiche.forEach(domanda -> {
			risposte.forEach(risposta -> {
				if (domanda.getId().equals(risposta.getCodiceDomanda())) {
					domanda.setValore(risposta.getValore());
				}
			});
		});

	}

	public String calculateValore(String formato, RispostaDB rispostaDB) {
		if (Arrays.asList("STR", "BOL", "DOM").contains(formato)) {
			return rispostaDB.getValoreStringa();
		} else if ("TXT".equals(formato)) {
			return rispostaDB.getValoreTestuale();
		} else if ("NUM".equals(formato)) {
			return rispostaDB.getValoreNumerico() == null ? null : rispostaDB.getValoreNumerico().toString();
		}
		throw new RuntimeException("Formato invalido");
	}

	public void caricaDomini(List<DomandaDTO> caratteristiche) {
		caratteristiche.forEach(domanda -> {
			if ("DOM".equals(domanda.getFormato())) {
				domanda.setDominio(this.dominioMapper.toListDTO(this.dominioDao.findByCodiceDominio(domanda.getCodiceDominio())));
			}

		});

	}

	public List<DomandaDTO> aggiungiDisegnoEOrdinaLista(List<DomandaDTO> domande, List<QstTemplatesDTO> disegni) {
		List<DomandaDTO> result = new ArrayList<>();
		disegni.stream().forEach(disegno -> {
			Optional<DomandaDTO> domandaOpt = domande.stream().filter(d -> d.getId().equals(disegno.getCodiceDomanda())).findFirst();
			if (domandaOpt.isPresent()) {
				DomandaDTO domanda = domandaOpt.get();
				domanda.setOrder(disegno.getOrdine());
				domanda.setObbligatorio(disegno.getObbligatorio());
				result.add(domanda);
			}
		});
		return result.stream().sorted(Comparator.comparingInt(DomandaDTO::getOrder)).collect(Collectors.toList());
	}

}
