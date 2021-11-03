package org.andreidodu.tbot.workers;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.converter.ItemConverter;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.PostDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.service.ConfigurationService;
import org.andreidodu.tbot.service.PostService;
import org.andreidodu.tbot.service.StatusService;
import org.andreidodu.tbot.telegram.TelegramUtil;
import org.andreidodu.tbot.util.DateUtils;
import org.andreidodu.tbot.util.ExceptionUtils;
import org.andreidodu.tbot.util.StringUtils;
import org.andreidodu.tbot.workers.rssbot.filters.RssBotItemFilter;
import org.andreidodu.tbot.workers.rssbot.util.RssBotMessageUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.vdurmont.emoji.EmojiParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RSSBotWorker implements BotWorker {

	private static final int ONE_SECOND = 1 * 1000;
	private final static String WORKER_TYPE = "rss_feed_reader";

	@Autowired
	private TelegramUtil telegramUtil;

	@Autowired
	private DateUtils dateUtils;

	@Autowired
	private RssBotMessageUtils rssBotMessageUtils;

	@Autowired
	private ExceptionUtils exceptionUtils;

	@Autowired
	private List<RssBotItemFilter> itemFilters;

	@Autowired
	private ItemConverter itemConverter;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private PostService postService;

	@Autowired
	private StatusService statusService;

	@Override
	public Runnable getRunnable(String botName) {
		return () -> {
			try {
				ConfigurationDTO configuration = this.configurationService.loadConfiguration(botName);

				Boolean isBotActive = BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_INACTIVE));
				List<String> chatIds = StringUtils.asList(configuration.loadCustomValue(WorkerConst.CS_CHAT_ID), Pattern.quote("|"));
				String botToken = configuration.loadCustomValue(WorkerConst.CS_BOT_TOKEN);
				String botType = configuration.loadCustomValue(WorkerConst.CS_BOT_TYPE);
				List<UrlInternalDTO> rssUrls = configuration.loadUrls();
				Boolean isToHash = BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_SAVE_HASHED, "true"));
				Boolean isInitialRssCopyEnabled = BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_COPY_INITIAL_RSS_TO_SAVE_FILE));
				Integer shortSleepInterval = Integer.valueOf(configuration.loadCustomValue(WorkerConst.CS_BOT_MESSAGE_INTER_CHAT_INTERVAL, "10")) * 1000;
				Boolean isSendHTMLMessage = BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_BOT_MESSAGE_HTML, "false"));
//				boolean isExperimentalEnabled = BooleanUtils.toBoolean(configuration.loadCustomValue(configPrefix, WorkerConst.CS_BOT_EXPERIMENTAL_ENABLED, "false"));
				List<String> staticMessages = StringUtils.asList(configuration.loadCustomValue(WorkerConst.CS_BOT_MESSAGE_CUSTOM_MESSAGE_LIST), Pattern.quote("|"));
				final int publichAfterNPostPublished = Integer.valueOf(configuration.loadCustomValue(WorkerConst.CS_BOT_MESSAGE_CUSTOM_MESSAGE_AFTER_X_POST_PUBLISHED, "4"));

				boolean isCleanFileEnabled = BooleanUtils.toBoolean(configuration.loadCustomValue(WorkerConst.CS_IS_CLEAN_FILE_ENABLED, "false"));
				List<String> inactivityInterval = StringUtils.asList(configuration.loadCustomValue(WorkerConst.CS_BOT_INACTIVITY_INTERVAL, ""), Pattern.quote("-"));
				List<String> silentInterval = StringUtils.asList(configuration.loadCustomValue(WorkerConst.CS_BOT_SILENT_INTERVAL, ""), Pattern.quote("-"));
				Integer threadSleepTime = Integer.valueOf(configuration.loadCustomValue(WorkerConst.CS_CHECK_INTERVAL, "60"));

				if (!isBotActive) {
					logga("Bot disattivato " + configuration.getCodiceBot());
					return;
				}

				this.telegramUtil.initializeBot(configuration, "RSS");

				if (isInitialRssCopyEnabled) {
					initialRSSCopy(botName, rssUrls, isToHash, shortSleepInterval);
				}

				logga("----------------------------------------------------------------------------------");
				logga("bot [" + botName + "] initialized successfully!");
				logga("----------------------------------------------------------------------------------");

				int staticMessageIndex = 0;
				int idxUrl = 0;
				int seconds = 0;
				while (true) {
					++seconds;

					if (!inactivityInterval.isEmpty() && this.dateUtils.isNowInInterval(botName, inactivityInterval.get(0), inactivityInterval.get(1))) {
						if (seconds % 60 == 0) {
							logga(String.format(EmojiParser.parseToUnicode(":x:") + " bot is inactive between %s and %s", inactivityInterval.get(0), inactivityInterval.get(1)));
						}
					} else {
						if (seconds % threadSleepTime == 0) {

							UrlInternalDTO urlInternal = rssUrls.get(idxUrl);

							configuration = this.reloadCofigurationEventualy(configuration, urlInternal);

							String url = urlInternal.getUrl();
							logga("URL in processamento: " + url);
							try {

								boolean isSourceEnabled = BooleanUtils.toBoolean(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_IS_SOURCE_ENABLED));
								if (!isSourceEnabled) {
									logga(String.format(EmojiParser.parseToUnicode(":exclamation:") + " Fonte ignorata da impostazioni [%s]", url));
								} else {

									boolean enableSilent = false;
									if (!silentInterval.isEmpty() && this.dateUtils.isNowInInterval(botName, silentInterval.get(0), silentInterval.get(1))) {
										enableSilent = true;
										logga("Silent mode enabled");
										logga(String.format(EmojiParser.parseToUnicode(":exclamation:") + " bot is in silent mode between %s and %s", silentInterval.get(0), silentInterval.get(1)));
									}

									boolean disableWebPagePreview = false;

									RssReader reader = new RssReader();
									logga("-------------> Calling URL " + url + "....");
									List<Item> newRssItems = reader.retrieveItems(url);
									logga("-------------> URL called successfully");
									List<Item> rawItems = this.itemConverter.clone(newRssItems);
									logga(EmojiParser.parseToUnicode(":part_alternation_mark:") + " total: [" + newRssItems.size() + "] items");

									logga("processing feed rss url: [" + url + "]");

									List<PostDTO> allRssPost = newRssItems.stream().map(item -> this.itemConverter.convertToPost(item, isToHash, urlInternal.getCodiceQuestionario())).collect(Collectors.toList());

									List<PostDTO> notProcessedItemsPost = this.postService.calculateNotProcessedItems(urlInternal, allRssPost);

									List<Item> notProcessedItems = newRssItems.stream().filter(item -> notProcessedItemsPost.contains(this.itemConverter.convertToPost(item, isToHash, urlInternal.getCodiceQuestionario()))).collect(Collectors.toList());

									logga("not processed: [" + notProcessedItems.size() + "] items");

									for (RssBotItemFilter filter : this.itemFilters) {
										notProcessedItems = filter.filter(configuration, urlInternal, notProcessedItems);
									}

									logga("to process: [" + notProcessedItems.size() + "] items");

									if (isCleanFileEnabled) {
//										cleanFile(configuration, configPrefix, filename, rssStatus, isToHash, idxUrl, newRssItems, notProcessedItems);
									}

									List<String> dateFormats = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_FORMAT), Pattern.quote("|"));
									String dateLocal = configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_LOCALE);
									orderByDateAsc(botName, notProcessedItems, dateFormats, dateLocal);
									logga(EmojiParser.parseToUnicode(":four_leaf_clover:") + " the bot has [" + notProcessedItems.size() + "] remaining messages to be published");

									if (!notProcessedItems.isEmpty()) {
										long publishedPostCount = this.postService.count(urlInternal).longValue();
										Item item = notProcessedItems.get(0);
										String message = this.rssBotMessageUtils.composeHTMLMessage(configuration, urlInternal, item);
										message = EmojiParser.parseToUnicode(message);

										if (chatIds.size() > 1) {
											for (String chatId : chatIds) {
												sendMessage(botName, isSendHTMLMessage, enableSilent, disableWebPagePreview, chatId, message);
												logga("i have sent the following message to the chatId [" + chatId + "]: " + (item.getTitle().isPresent() ? item.getTitle().get() : message));
												staticMessageIndex = sendEventuallyStaticMessage(chatId, botName, staticMessages, publichAfterNPostPublished, publishedPostCount, staticMessageIndex, enableSilent, disableWebPagePreview);
											}
										} else if (!chatIds.isEmpty()) {
											String chatId = chatIds.get(0);
											sendMessage(botName, isSendHTMLMessage, enableSilent, disableWebPagePreview, chatId, message);
											logga("i have sent the following message to the chatId [" + chatId + "]: " + (item.getTitle().isPresent() ? item.getTitle().get() : message));
											staticMessageIndex = sendEventuallyStaticMessage(chatId, botName, staticMessages, publichAfterNPostPublished, publishedPostCount, staticMessageIndex, enableSilent, disableWebPagePreview);
										}

										PostDTO publishedPost = this.itemConverter.convertToPost(item, isToHash, urlInternal.getCodiceQuestionario());
										this.postService.save(urlInternal, publishedPost);
									}

									if (chatIds.size() > 1) {
										for (String chatId : chatIds) {
											sendEventuallySummaryHTMLMessage(configuration, urlInternal, rawItems, dateFormats, dateLocal, chatId, enableSilent);
										}
									} else if (!chatIds.isEmpty()) {
										String chatId = chatIds.get(0);
										sendEventuallySummaryHTMLMessage(configuration, urlInternal, rawItems, dateFormats, dateLocal, chatId, enableSilent);
									}

									logga("processing url [" + url + "] ended");
								}
							} catch (IOException ee) {
								logga("si è verificata una eccezione mentre si contattava l'url: " + url);
								logga(ee.getMessage(), ee);
							}
							logga("End loop for the bot [" + botName + "] wating for: " + threadSleepTime);
							logga("----------------------------------------------------------------------------------");

							idxUrl++;
							if (idxUrl >= rssUrls.size()) {
								idxUrl = 0;
							}
						} else {
							if (this.dateUtils.toSeconds(new Date()) == 0) {
								for (UrlInternalDTO urlInternal : rssUrls) {
									this.statusService.insertOrUpdate(urlInternal, "publishedSummary", "false");
								}
							}
						}
					}

					/**************************************************************/

					Thread.sleep(ONE_SECOND);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logga("Huston, we have an Exception with the bot: " + botName, e);
			}
			logga("----------------------------------------------------------------------------------");
			logga("bot [" + botName + "] died");
			logga("----------------------------------------------------------------------------------");
		};
	}

	private ConfigurationDTO reloadCofigurationEventualy(ConfigurationDTO configuration, UrlInternalDTO urlInternal) {
		final String allConfigDaAggiornare = this.configurationService.loadValueImmediately(configuration.getCodiceQuestionarioBot(), WorkerConst.RELOAD_ALL_CONFIGURATION);
		if (WorkerConst.RELOAD_CONFIGURATION_VALUE_DA_AGGIORNARE.equals(allConfigDaAggiornare)) {
			logga("è stato richiesto il ricarimento di tutte le impostazioni del bot " + configuration.getCodiceBot() + " ....");
			ConfigurationDTO cNew = this.configurationService.loadConfiguration(configuration.getCodiceBot());
			configuration.setBotConfiguration(cNew.getBotConfiguration());
			configuration.setUrlsConfiguration(cNew.getUrlsConfiguration());
			this.configurationService.aggiornaStatoCaricamentoConfigurazione(configuration.getCodiceQuestionarioBot(), WorkerConst.RELOAD_ALL_CONFIGURATION, WorkerConst.RELOAD_CONFIGURATION_VALUE_AGGIORNATO);
			logga("importazioni del bot " + configuration.getCodiceBot() + " ricaricate");
		} else {
			final String botConfigDaAggiornare = this.configurationService.loadValueImmediately(configuration.getCodiceQuestionarioBot(), WorkerConst.RELOAD_BOT_CONFIGURATION);
			if (WorkerConst.RELOAD_CONFIGURATION_VALUE_DA_AGGIORNARE.equals(botConfigDaAggiornare)) {
				logga("è stato richiesto il ricarimento delle impostazioni del bot " + configuration.getCodiceBot() + " ....");
				configuration.setBotConfiguration(this.configurationService.loadBotConfigurationMap(configuration.getCodiceQuestionarioBot()));
				logga("importazioni del bot " + configuration.getCodiceBot() + " ricaricate");
				this.configurationService.aggiornaStatoCaricamentoConfigurazione(configuration.getCodiceQuestionarioBot(), WorkerConst.RELOAD_BOT_CONFIGURATION, WorkerConst.RELOAD_CONFIGURATION_VALUE_AGGIORNATO);
			}
			final String urlConfigDaAggiornare = this.configurationService.loadValueImmediately(urlInternal.getCodiceQuestionario(), WorkerConst.RELOAD_URL_CONFIGURATION);
			if (WorkerConst.RELOAD_CONFIGURATION_VALUE_DA_AGGIORNARE.equals(urlConfigDaAggiornare)) {
				logga("è stato richiesto il ricarimento delle impostazioni della url " + urlInternal.getUrl() + " ....");
				configuration.getUrlsConfiguration().put(urlInternal.getCodiceQuestionario(), this.configurationService.loadRisposteByCodiceQuestionario(urlInternal.getCodiceQuestionario()));
				logga("impostazioni della url " + urlInternal.getUrl() + " caricate con successo....");
				this.configurationService.aggiornaStatoCaricamentoConfigurazione(urlInternal.getCodiceQuestionario(), WorkerConst.RELOAD_URL_CONFIGURATION, WorkerConst.RELOAD_CONFIGURATION_VALUE_AGGIORNATO);
			}
		}
		return configuration;
	}

	private void initialRSSCopy(String configPrefix, List<UrlInternalDTO> rssUrls, Boolean isToHash, Integer shortSleepInterval) throws IOException, InterruptedException {
		for (UrlInternalDTO urlInternal : rssUrls) {
			String url = urlInternal.getUrl();
			RssReader reader = new RssReader();
			List<Item> rssFeed = reader.retrieveItems(url);
			List<PostDTO> posts = rssFeed.stream().map(item -> this.itemConverter.convertToPost(item, isToHash.booleanValue(), urlInternal.getCodiceQuestionario())).collect(Collectors.toList());
			this.postService.saveIfDoesNotExists(urlInternal, posts);
			Thread.sleep(shortSleepInterval.longValue());
		}
	}

	private void sendMessage(String configPrefix, Boolean isSendHTMLMessage, boolean enableSilent, boolean disableWebPagePreview, String chatId, String message) {
		if (isSendHTMLMessage.booleanValue()) {
			this.telegramUtil.sendHTMLMessage(configPrefix, chatId, message, enableSilent, disableWebPagePreview);
		} else {
			this.telegramUtil.sendMessage(configPrefix, chatId, message);
		}
	}

	private void sendEventuallySummaryHTMLMessage(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> rawItems, List<String> dateFormats, String dateLocal, String chatId, boolean enableSilent) {
		logga("checking if I eventually shoud send summary message....");
		String url = urlInternal.getUrl();
		String configPrefix = urlInternal.getCodiceBot();
		if (BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_PUBLISH_SUMMARY, "false"))) {
			logga("yeah, it is time to send it...");
			// 11:00:00
			String publishSummaryTime = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_PUBLISH_SUMMARY_TIME, "20:00:00");
			logga(String.format("Publish time: %s", publishSummaryTime));
			try {
				if (this.dateUtils.toSeconds(new Date()) >= this.dateUtils.toSeconds(publishSummaryTime) && !BooleanUtils.toBoolean(this.statusService.get(urlInternal, "publishedSummary"))) {
					logga("filtering summary list...");
					for (RssBotItemFilter filter : this.itemFilters) {
						rawItems = filter.filter(configuration, urlInternal, rawItems);
					}
					orderByDateAsc(configPrefix, rawItems, dateFormats, dateLocal);
					String message = this.rssBotMessageUtils.composeSummaryHTMLMessage(configuration, urlInternal, rawItems, this.rssBotMessageUtils.retrieveSource(configuration, urlInternal, url));
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(message)) {
						logga("sending message....");
						sendMessage(configPrefix, true, enableSilent, true, chatId, message);
						this.statusService.insertOrUpdate(urlInternal, "publishedSummary", "true");
						logga("sent summary message");
					}
				}
			} catch (ParseException e) {
				logga("Error", e);
			}
		}
	}

	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public int sendEventuallyStaticMessage(String chatId, String configPrefix, List<String> staticMessages, final int publichAfterNPostPublished, long postPublished, int staticMessageIndex, boolean enableSilentMode, boolean disableWebPagePreview) {
		logga("staticMessages: " + staticMessages);
		logga("staticMessages.size(): " + staticMessages.size());
		logga("postPublished: " + postPublished);
		logga("publichAfterNPostPublished: " + publichAfterNPostPublished);
		logga("publish static message: " + (postPublished % publichAfterNPostPublished == 0));
		if (!staticMessages.isEmpty() && postPublished % publichAfterNPostPublished == 0) {
			logga("publishing static message: " + EmojiParser.parseToUnicode(staticMessages.get(staticMessageIndex)));
			this.telegramUtil.sendHTMLMessage(configPrefix, chatId, EmojiParser.parseToUnicode(staticMessages.get(staticMessageIndex)), enableSilentMode, disableWebPagePreview);
			staticMessageIndex++;
			if (staticMessageIndex >= staticMessages.size()) {
				staticMessageIndex = 0;
			}
		}
		return staticMessageIndex;
	}

	private void orderByDateAsc(String configPrefix, List<Item> notProcessedItems, List<String> dateFormats, String dateLocal) {
		logga("ordinamento per data della lista in modo ascendente...");
		DateUtils dateUtils = this.dateUtils;
		Collections.sort(notProcessedItems, new Comparator<Item>() {
			@Override
			public int compare(Item p1, Item p2) {
				for (String dateFormat : dateFormats) {
					try {
						return dateUtils.toDate(dateFormat, dateLocal, p1.getPubDate().get()).compareTo(dateUtils.toDate(dateFormat, dateLocal, p2.getPubDate().get()));
					} catch (ParseException e) {
						logga("Errore di parsing della data con pattern [" + dateFormat + "] della data [" + p1.getPubDate().get() + "][" + p2.getPubDate().get() + "]: tutto ok: " + e.getMessage());
					}
				}
				return 0;
			}

		});
	}

	protected void logga(String string) {
		log.info(string);
	}

	private void logga(String string, Throwable e) {
		log.info("[" + e.getMessage() + "] " + string);
		String sb = this.exceptionUtils.getStackTrace(e);
		log.info(sb);
	}

	@Override
	public String getType() {
		return WORKER_TYPE;
	}

}
