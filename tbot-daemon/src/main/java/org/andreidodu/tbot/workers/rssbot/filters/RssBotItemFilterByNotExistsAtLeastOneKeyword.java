package org.andreidodu.tbot.workers.rssbot.filters;

import java.util.List;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.util.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;

@Component
@Order(value = 30)
public class RssBotItemFilterByNotExistsAtLeastOneKeyword extends RssBotItemFilterByKeyword implements RssBotItemFilter {

	@Override
	public List<Item> filter(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items) {
		List<String> filterStringsNotContainsInOr = StringUtils
						.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_BOT_MESSAGE_FILTER_NOT_CONTAINS), Pattern.quote("|"));
		return filterByExistAtLeastOneKeyword(configuration, items, filterStringsNotContainsInOr, false);
	}

}
