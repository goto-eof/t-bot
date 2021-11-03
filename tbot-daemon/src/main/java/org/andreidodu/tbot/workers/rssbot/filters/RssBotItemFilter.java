package org.andreidodu.tbot.workers.rssbot.filters;

import java.util.List;

import org.andreidodu.tbot.dto.ConfigurationDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;

import com.apptastic.rssreader.Item;

public interface RssBotItemFilter {

	List<Item> filter(ConfigurationDTO configuration, UrlInternalDTO urlInternal, List<Item> items);
}
