package org.andreidodu.tbot.service;

import java.util.List;

import org.andreidodu.tbot.dto.PostDTO;
import org.andreidodu.tbot.dto.UrlInternalDTO;

public interface PostService {

	void saveIfDoesNotExists(UrlInternalDTO urlInternal, List<PostDTO> posts);

	PostDTO save(UrlInternalDTO urlInternal, PostDTO dto);

	List<PostDTO> calculateNotProcessedItems(UrlInternalDTO urlInternal, List<PostDTO> allRss);

	Long count(UrlInternalDTO utlInternal);

}
