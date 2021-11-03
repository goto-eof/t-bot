package org.andreidodu.tbot.workers.rssbot.filters;

import java.util.List;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.service.ConfigurationService;
import org.andreidodu.tbot.util.ConfigUtil;
import org.andreidodu.tbot.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;

@Component
@Order(value = 20)
public class RssBotItemFilterByExistsAtLeastOneKeyword extends RssBotItemFilterByKeyword implements RssBotItemFilter {

	@Autowired
	private ConfigUtil configUtil;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public List<Item> filter(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items) {
		List<String> filterStringsInOr = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_BOT_MESSAGE_FILTER_CONTAINS),
						Pattern.quote("|"));
		return filterByExistAtLeastOneKeyword(configuration, items, filterStringsInOr, true);
	}

}
