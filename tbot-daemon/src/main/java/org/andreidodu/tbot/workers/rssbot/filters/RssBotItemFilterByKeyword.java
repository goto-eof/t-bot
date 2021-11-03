package org.andreidodu.tbot.workers.rssbot.filters;

import java.util.Iterator;
import java.util.List;

import org.andreidodu.tbot.converter.ItemConverter;
import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.workers.rssbot.util.RssBotMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.apptastic.rssreader.Item;

public abstract class RssBotItemFilterByKeyword {

	@Autowired
	private RssBotMessageUtils rssBotMessageUtils;

	@Autowired
	private ItemConverter itemConverter;

	protected List<Item> filterByExistAtLeastOneKeyword(ConfigurationDTO configuration, List<Item> itemsIn, List<String> filterStringsInOr,
					boolean containsOrNotContains) {
		if (filterStringsInOr.isEmpty()) {
			return itemsIn;
		}
		List<Item> notProcessedItems = this.itemConverter.clone(itemsIn);
		Iterator<Item> iter = notProcessedItems.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			boolean containsAtLeastOneKeyword = false;
			String bigMessage = this.rssBotMessageUtils.composeBigMessage(item, false);
			for (String keyword : filterStringsInOr) {
				if (bigMessage.toLowerCase().contains(keyword.toLowerCase())) {
					containsAtLeastOneKeyword = true;
					break;
				}
			}
			if (!containsAtLeastOneKeyword && containsOrNotContains) {
				iter.remove();
			} else if (containsAtLeastOneKeyword && !containsOrNotContains) {
				iter.remove();
			}
		}
		return notProcessedItems;
	}

}
