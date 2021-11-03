package org.andreidodu.tbot.workers.rssbot.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DateTimeException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.util.DateUtils;
import org.andreidodu.tbot.util.ExceptionUtils;
import org.andreidodu.tbot.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;
import com.vdurmont.emoji.EmojiParser;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssBotMessageUtils {

	@Autowired
	private DateUtils dateUtils;

	@Autowired
	private ExceptionUtils exceptionUtils;

	public String composeHTMLMessage(ConfigurationDTO configuration, UrlInternalDTO urlInternal, Item post) {

		String titlePrefix = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_TITLE_PREFIX, "");
		String descriptionPrefix = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_DESCRIPTION_PREFIX, "");
		String linkPrefix = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_LINK_PREFIX, "");

		Boolean removeDescriptionHtmlTags = BooleanUtils
						.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_DESCRIPTION_REMOVE_HTML_TAGS, "true"));

		String message = "";
		if (post.getTitle().isPresent()
						&& BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_TITLE, "true"))) {
			message += titlePrefix + "<b>" + post.getTitle().get() + "</b>. ";
		}

		Boolean showSource = BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_SOURCE, "false"))
						&& post.getLink().isPresent();
		Boolean showDate = BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_DATE, "false"))
						&& post.getPubDate().isPresent();

		if (showDate && showSource) {
			String date = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_DATE_ICON, retrieveDateFromItem(configuration, urlInternal, post));
			String urlStr = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_SOURCE_ICON,
							retrieveSource(configuration, urlInternal, post.getLink().get()));
			message += String.format("( %s | %s )", date, urlStr);
		} else if (showDate) {
			String date = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_DATE_ICON, retrieveDateFromItem(configuration, urlInternal, post));
			message += String.format("( %s )", date);
		} else if (showSource) {
			String urlStr = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_SOURCE_ICON,
							retrieveSource(configuration, urlInternal, post.getLink().get()));
			message += String.format("( %s )", urlStr);
		}
		message += "\n";
		Integer maxDescriptionLength = Integer
						.valueOf(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_MAX_DESCRIPTION_LENGTH, "1000"));
		if (post.getDescription().isPresent() && BooleanUtils
						.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_DESCRIPTION, "false"))) {
			String description = post.getDescription().get();
			logga(urlInternal.getCodiceBot(), "La descrizione è la seguente: " + description);
			logga(urlInternal.getCodiceBot(), "Stato remove html tags: " + removeDescriptionHtmlTags);

			if (removeDescriptionHtmlTags) {
				logga(urlInternal.getCodiceBot(), "Sto rimuovendo li html tags dalla descrizione");
				description = description.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
				// removes empty tags
				description = description.replaceAll("<([^>]*)></\\1>", "");
				logga(urlInternal.getCodiceBot(), "Risultato rimozione: " + description);
			}
			if (description.length() > maxDescriptionLength) {
				logga(urlInternal.getCodiceBot(), "La descrizione supera la dimensione massiam di caratteri ammissibili: " + maxDescriptionLength);
				description = description.substring(0, maxDescriptionLength) + "...";
				logga(urlInternal.getCodiceBot(), "ho troncato la stringa a: " + description);
			}
			if (!org.apache.commons.lang3.StringUtils.isBlank(description)) {
				message += descriptionPrefix + "<em>" + description + "</em>\n";
			}
		} else {
			message += "\n";
		}

		List<String> listOfTags = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_BOT_MESSAGE_TAG_WORDS), Pattern.quote("|"));
		String bigMessage = composeBigMessage(post, false);
		if (BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_TAGS, "false"))) {
			if (post.getLink().isPresent()) {
				message += urlToTag(post.getLink().get());
			}
			message += generateTags(bigMessage, listOfTags, true);

		}

		message += "\n\n\n";
		if (post.getLink().isPresent()
						&& BooleanUtils.toBoolean(configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_MESSAGE_SHOW_LINK, "true"))) {
			message += linkPrefix + post.getLink().get();
		}
		return message;
	}

	public static void main(String argv[]) {
		String description = "<p></p>";
		description = description.replaceAll("<([^>]*)></\\1>", "");
		System.out.println("[" + description + "]");

	}

	private String retrieveDateFromItem(ConfigurationDTO configuration, UrlInternalDTO urlInternal, Item post) {
		List<String> dateFormatsFrom = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_FORMAT), Pattern.quote("|"));
		String dateLocalFrom = configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_LOCALE);
		String dateFormatTo = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_MESSAGE_DATE_FORMAT, "dd MM yyyy");
		String dateLocalTo = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_MESSAGE_DATE_LOCALE, "UK");
		String dateStr = post.getPubDate().get();
		String humanableDate = "";
		for (String dateFormatFrom : dateFormatsFrom) {
			try {
				humanableDate = this.dateUtils.formateDateHumanable(dateStr, dateFormatFrom, dateLocalFrom, dateFormatTo, dateLocalTo);
				break;
			} catch (DateTimeException e) {
				logga(configuration.getCodiceBot(), "DateTimeException: tutto ok: " + e.getMessage());
				humanableDate = dateStr;
			}
		}
		return humanableDate;
	}

	private String addIcon(ConfigurationDTO configuration, UrlInternalDTO urlInternal, String configSuffixMessageIconKey, String str) {
		String icon = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, configSuffixMessageIconKey, "");
		if (org.apache.commons.lang3.StringUtils.isNotBlank(icon)) {
			return EmojiParser.parseToUnicode(icon) + " " + str;
		}
		return str;
	}

	public String retrieveSource(ConfigurationDTO configuration, UrlInternalDTO urlInternal, String link) {
		try {
			URL url = new URL(link);
			return url.getHost().replace("www.", "");
		} catch (MalformedURLException e) {
			logga(configuration.getCodiceBot(), "Errore formattazione data", e);
		}
		return "";
	}

	public String urlToTag(String link) {
		try {
			URL url = new URL(link);
			String toProcess = url.getHost().replace("www.", "");
			String result = "";
			String[] arrDot = toProcess.split(Pattern.quote("."));
			for (int i = 0; i < arrDot.length; i++) {
				String str = arrDot[i];
				if (i != arrDot.length - 1 && !str.contains("-")) {
					result += str.substring(0, 1).toUpperCase() + str.substring(1);
				} else if (str.contains("-")) {
					result += stringMergeToUpperCaseFirstChar(str, Pattern.quote("-"));
				}
			}
			return "#" + result + " ";
		} catch (MalformedURLException e) {
			System.out.print("Errore " + e.getMessage());
		}

		return null;
	}

	public String composeBigMessage(Item item, boolean includeLink) {
		String bigMessage = "";
		if (item.getTitle().isPresent()) {
			bigMessage += item.getTitle().get() + " ";
		}
		if (item.getDescription().isPresent()) {
			bigMessage += item.getDescription().get() + " ";
		}
		if (includeLink && item.getLink().isPresent()) {
			bigMessage += item.getLink().get() + " ";
		}
		return bigMessage;
	}

	private String generateTags(String bigMessage, List<String> listOfTags, boolean isCaseSensitive) {
		String result = "";
		Set<String> resultTags = new HashSet<>();
		for (String tagName : listOfTags) {
			if (isCaseSensitive && bigMessage.contains(tagName)) {
				resultTags.add(preprocessTag(tagName));
			} else if (!isCaseSensitive && bigMessage.toLowerCase().contains(tagName.toLowerCase())) {
				resultTags.add(preprocessTag(tagName));
			}
		}

		Iterator<String> iter = resultTags.iterator();
		while (iter.hasNext()) {
			String tag = iter.next();
			result += "#" + tag + " ";
		}

		return result;
	}

	private String preprocessTag(String tagName) {
		String result = stringMergeToUpperCaseFirstChar(tagName, Pattern.quote("-"));
		result = stringMergeToUpperCaseFirstChar(result, " ");
		return result;
	}

	private String stringMergeToUpperCaseFirstChar(String tagName, String quote) {
		String result = "";
		String[] arr = tagName.split(quote);
		for (String str : arr) {
			if (str.length() > 0) {
				result += str.substring(0, 1).toUpperCase() + str.substring(1);
			}
		}
		return result;
	}

	protected void logga(String configPrefix, String string) {
		log.debug("[" + configPrefix + "] " + string);
	}

	private void logga(String configPrefix, String string, Throwable e) {
		log.debug("[" + configPrefix + "][" + e.getMessage() + "] " + string);
		String sb = this.exceptionUtils.getStackTrace(e);
		log.debug("[" + configPrefix + "] " + sb);
	}

	public String composeSummaryHTMLMessage(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items, String link) {
		String result = null;

		if (!items.isEmpty()) {
			String summaryIntroduction = EmojiParser.parseToUnicode(":pushpin:") + " " + configuration.loadUrlBotValueCustomOrGlobal(urlInternal,
							WorkerConst.CS_BOT_PUBLISH_SUMMARY_INTRODUCTION, "What happened today from the website");
			String todayDate = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_DATE_ICON, this.dateUtils.todayToString());
			String processedLink = addIcon(configuration, urlInternal, WorkerConst.CS_MESSAGE_SOURCE_ICON, link);
			result = String.format("%s (%s | %s)\n\n", summaryIntroduction, todayDate, processedLink);

			String readMore = configuration.loadUrlBotValueCustomOrGlobal(urlInternal, WorkerConst.CS_BOT_PUBLISH_SUMMARY_READ_MORE, "Read more...");
			List<String> icons = Arrays.asList(":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:",
							":keycap_ten:");
			for (int i = 0; i < icons.size() && items != null && i < items.size(); i++) {
				Item item = items.get(i);
				if (item.getTitle().isPresent() && item.getLink().isPresent()) {
					String title = EmojiParser.parseToUnicode(icons.get(i)) + " <em>" + item.getTitle().get() + "</em> (<a href=\"" + item.getLink().get()
									+ "\">" + readMore + "</a>)";
					result += title + "\n\n";
				}
			}
		}

		return result;
	}

}
