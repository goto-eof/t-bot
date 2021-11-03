package org.andreidodu.tbot.workers.rssbot.filters;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.converter.ItemConverter;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.util.DateUtils;
import org.andreidodu.tbot.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;

@Component
@Order(value = 10)
public class RssBotMessageFilterByTodayDate implements RssBotItemFilter {

	@Autowired
	private DateUtils dateUtils;

	@Autowired
	private ItemConverter itemConverter;

	@Override
	public List<Item> filter(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items) {
		List<String> dateFormats = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_FORMAT), Pattern.quote("|"));
		String dateLocal = configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_DATE_LOCALE);
		return filterByTodayDate(configuration, urlInternal, dateFormats, dateLocal, items);
	}

	private List<Item> filterByTodayDate(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<String> dateFormats, String dateLocal,
					List<Item> itemsIn) {
		List<Item> items = this.itemConverter.clone(itemsIn);
		if (BooleanUtils.toBoolean(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_FILTER_BY_TODAY_DATE))) {
			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				if (item.getPubDate().isPresent()) {
					String dateStr = null;
					try {
						dateStr = item.getPubDate().get();
						String otherDate = calculateHumanableDate(dateFormats, dateLocal, dateStr);
						DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY);
						String today = outputFormatter.format(LocalDate.now());
						if (!today.equals(otherDate)) {
							iter.remove();
						}
					} catch (DateTimeException e) {
						// logga(configPrefix, "Errore parsing della data [" + dateStr + "][" +
						// dateFormats + "][" + dateLocal + "]: " + e.getMessage());
					}
				}
			}
		}
		return items;
	}

	private String calculateHumanableDate(List<String> dateFormats, String dateLocal, String dateStr) {
		for (String dateFormat : dateFormats) {
			try {
				String date = this.dateUtils.formateDate(dateStr, dateFormat, dateLocal);
//				logga(configPrefix, String.format("Data parsata corettamente [%s][%s]", dateFormat, dateStr));
				return date;
			} catch (DateTimeParseException ee) {
				//
//				logga(configPrefix, String.format("Impossibile parsare la data. Tutto ok. [%s][%s]", dateFormat, dateStr));
			}
		}

		throw new DateTimeParseException("Impossibile parsare la data", dateStr, 0);
	}

}
