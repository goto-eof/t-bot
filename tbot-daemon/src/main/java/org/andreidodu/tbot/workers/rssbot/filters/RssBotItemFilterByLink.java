package org.andreidodu.tbot.workers.rssbot.filters;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.andreidodu.tbot.common.constants.WorkerConst;
import org.andreidodu.tbot.converter.ItemConverter;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;
import org.andreidodu.tbot.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;

@Component
@Order(value = 40)
public class RssBotItemFilterByLink extends RssBotItemFilterByKeyword implements RssBotItemFilter {

	@Autowired
	private ItemConverter itemConverter;

	@Override
	public List<Item> filter(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items) {
		List<String> filterExcludedLinksInOr = StringUtils.asList(configuration.loadUrlBotValue(urlInternal, WorkerConst.CS_ESCLUDED_LINKS), Pattern.quote("|"));
		return filterByExcludedLinksInOr(items, filterExcludedLinksInOr);
	}

	private List<Item> filterByExcludedLinksInOr(List<Item> itemsIn, List<String> filterExcludedLinksInOr) {
		List<Item> notProcessedItems = this.itemConverter.clone(itemsIn);
		Iterator<Item> iter = notProcessedItems.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			boolean containsString = false;
			for (String keyword : filterExcludedLinksInOr) {
				String bigMessage = item.getLink().get();
				if (bigMessage.toLowerCase().contains(keyword.toLowerCase())) {
					containsString = true;
					break;
				}
			}
			if (containsString) {
				iter.remove();
			}
		}
		return notProcessedItems;
	}

}
