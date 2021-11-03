package org.andreidodu.tbot.converter;

import java.util.ArrayList;
import java.util.List;

import org.andreidodu.tbot.dto.PostDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.apptastic.rssreader.Item;

@Component
public class ItemConverter {

	public List<Item> clone(List<Item> items) {
		List<Item> result = new ArrayList<>();
		for (Item item : items) {
			result.add(clone(item));
		}
		return result;
	}

	private Item clone(Item item) {
		Item result = new Item();
		if (item.getAuthor().isPresent()) {
			result.setAuthor(item.getAuthor().get());
		}
		if (item.getCategory().isPresent()) {
			result.setCategory(item.getCategory().get());
		}

		result.setChannel(item.getChannel());

		if (item.getDescription().isPresent()) {
			result.setDescription(item.getDescription().get());
		}

		if (item.getGuid().isPresent()) {
			result.setGuid(item.getGuid().get());
		}
		if (item.getIsPermaLink().isPresent()) {
			result.setIsPermaLink(item.getIsPermaLink().get());
		}
		if (item.getLink().isPresent()) {
			result.setLink(item.getLink().get());
		}
		if (item.getPubDate().isPresent()) {
			result.setPubDate(item.getPubDate().get());
		}
		if (item.getTitle().isPresent()) {
			result.setTitle(item.getTitle().get());
		}
		return result;
	}

	public PostDTO convertToPost(Item post, boolean isToHash, String codiceQuestionario) {
		PostDTO publishedPost = new PostDTO();

		if (post.getTitle().isPresent()) {
			String title = hash(isToHash, post.getTitle().get());
			publishedPost.setTitle(title);
		}
		if (post.getDescription().isPresent()) {
			String description = hash(isToHash,
							post.getDescription().get().length() > 255 ? post.getDescription().get().substring(0, 255) : post.getDescription().get());
			publishedPost.setDescription(description);
		}
		if (post.getLink().isPresent()) {
			String link = hash(isToHash, linkPreprocess(post.getLink().get()));
			publishedPost.setLink(link);
		}
		if (post.getPubDate().isPresent()) {
			String date = post.getPubDate().get();
			publishedPost.setDate(date);
		}

		publishedPost.setHash(hash(true, stringMerge(publishedPost.getTitle(), publishedPost.getDescription(), publishedPost.getLink(), publishedPost.getDate(),
						codiceQuestionario)));

		return publishedPost;
	}

	private String stringMerge(String... strs) {
		String result = "";
		for (String s : strs) {
			result += s == null ? "" : s;
		}
		return result;
	}

	private String hash(boolean hash, String str) {
		if (hash) {
			return DigestUtils.md5Hex(str).toUpperCase();
		}
		return str;
	}

	private String linkPreprocess(String str) {
		return str.replace("http://", "").replace("https://", "");
	}

}
