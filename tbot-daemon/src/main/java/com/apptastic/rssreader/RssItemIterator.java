package com.apptastic.rssreader;

import static javax.xml.stream.XMLStreamConstants.CDATA;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RssItemIterator implements Iterator<Item> {

	private InputStream is;
	private XMLStreamReader reader;
	private Channel channel;
	private Item item = null;
	private Item nextItem;
	private boolean isChannelPart = true;
	private String elementName = null;
	private StringBuilder textBuilder;
	private String url;

	protected void logga(Level lev, String string) {
		log.info(string);
	}

	protected void logga(Level lev, String string, Throwable e) {
		log.info(string);
		log.error(e.toString());
	}

	public RssItemIterator(InputStream is, String url) {
		this.is = is;
		this.nextItem = null;
		this.textBuilder = new StringBuilder();
		this.url = url;

		try {
			var xmlInFact = XMLInputFactory.newInstance();

			// disable XML external entity (XXE) processing
			xmlInFact.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			xmlInFact.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

			this.reader = xmlInFact.createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			logga(Level.WARNING, "Failed to process XML 1. ", e);
		}
	}

	void peekNext() {
		if (this.nextItem == null) {
			try {
				this.nextItem = next();
			} catch (NoSuchElementException e) {
				this.nextItem = null;
			}
		}
	}

	@Override
	public boolean hasNext() {
		peekNext();
		return this.nextItem != null;
	}

	@Override
	public Item next() {
		if (this.nextItem != null) {
			var next = this.nextItem;
			this.nextItem = null;

			return next;
		}

		try {
			while (this.reader != null && this.reader.hasNext()) {
				var type = this.reader.next(); // do something here

				if (type == CHARACTERS || type == CDATA) {
					parseCharacters();
				} else if (type == START_ELEMENT) {
					parseStartElement();
					parseAttributes();
				} else if (type == END_ELEMENT) {
					var itemParsed = parseEndElement();

					if (itemParsed) {
						return this.item;
					}
				}
			}
		} catch (XMLStreamException e) {
			logga(Level.WARNING, "------------> Failed to parse XML. [" + this.url + "]", e);
		}
		try {
			this.reader.close();
			this.is.close();
			logga(Level.INFO, "------------> IS and reader closed()...[" + this.url + "]");
		} catch (XMLStreamException | IOException e) {
			logga(Level.WARNING, "------------> Failed to close XML stream. [" + this.url + "]", e);
		}

		throw new NoSuchElementException();
	}

	void parseStartElement() {
		this.textBuilder.setLength(0);
		this.elementName = this.reader.getLocalName();

		if ("channel".equals(this.elementName) || "feed".equals(this.elementName)) {
			this.channel = new Channel();
			this.channel.setTitle("");
			this.channel.setDescription("");
			this.channel.setLink("");
			this.isChannelPart = true;
		} else if ("item".equals(this.elementName) || "entry".equals(this.elementName)) {
			this.item = new Item();
			this.item.setChannel(this.channel);
			this.isChannelPart = false;
		} else if ("guid".equals(this.elementName)) {
			var value = this.reader.getAttributeValue(null, "isPermaLink");
			if (this.item != null) {
				this.item.setIsPermaLink(Boolean.valueOf(value).booleanValue());
			}
		}
	}

	void parseAttributes() {
		if (this.reader.getLocalName().equals("link")) {
			var rel = this.reader.getAttributeValue(null, "rel");
			var link = this.reader.getAttributeValue(null, "href");
			var isAlternate = "alternate".equals(rel);

			if (link != null && isAlternate) {
				if (this.isChannelPart) {
					this.channel.setLink(link);
				} else {
					this.item.setLink(link);
				}
			}
		}
	}

	boolean parseEndElement() {
		var name = this.reader.getLocalName();
		var text = this.textBuilder.toString().trim();

		if (this.isChannelPart) {
			parseChannelCharacters(this.elementName, text);
		} else {
			parseItemCharacters(this.elementName, this.item, text);
		}

		this.textBuilder.setLength(0);

		return "item".equals(name) || "entry".equals(name);
	}

	void parseCharacters() {
		var text = this.reader.getText();

		if (text.trim().isEmpty()) {
			return;
		}

		this.textBuilder.append(text);
	}

	void parseChannelCharacters(String elementName, String text) {
		if (this.channel == null || text.isEmpty()) {
			return;
		}

		if ("title".equals(elementName)) {
			this.channel.setTitle(text);
		} else if ("description".equals(elementName) || "subtitle".equals(elementName)) {
			this.channel.setDescription(text);
		} else if ("link".equals(elementName)) {
			this.channel.setLink(text);
		} else if ("category".equals(elementName)) {
			this.channel.setCategory(text);
		} else if ("language".equals(elementName)) {
			this.channel.setLanguage(text);
		} else if ("copyright".equals(elementName) || "rights".equals(elementName)) {
			this.channel.setCopyright(text);
		} else if ("generator".equals(elementName)) {
			this.channel.setGenerator(text);
		} else if ("ttl".equals(elementName)) {
			this.channel.setTtl(text);
		} else if ("pubDate".equals(elementName)) {
			this.channel.setPubDate(text);
		} else if ("lastBuildDate".equals(elementName) || "updated".equals(elementName)) {
			this.channel.setLastBuildDate(text);
		} else if ("managingEditor".equals(elementName)) {
			this.channel.setManagingEditor(text);
		} else if ("webMaster".equals(elementName)) {
			this.channel.setWebMaster(text);
		}
	}

	void parseItemCharacters(String elementName, Item item, String text) {
		if (text.isEmpty()) {
			return;
		}

		if ("guid".equals(elementName) || "id".equals(elementName)) {
			item.setGuid(text);
		} else if ("title".equals(elementName)) {
			item.setTitle(text);
		} else if ("description".equals(elementName) || "summary".equals(elementName) || "content".equals(elementName)
						|| "content:encoded".contentEquals(elementName)) {
			item.setDescription(text);
		} else if ("link".equals(elementName)) {
			item.setLink(text);
		} else if ("author".equals(elementName)) {
			item.setAuthor(text);
		} else if ("category".equals(elementName)) {
			item.setCategory(text);
		} else if ("pubDate".equals(elementName) || "published".equals(elementName) || "date".equals(elementName)) {
			item.setPubDate(text);
		} else if ("updated".equals(elementName) && !item.getPubDate().isPresent()) {
			item.setPubDate(text);
		}
	}
}
