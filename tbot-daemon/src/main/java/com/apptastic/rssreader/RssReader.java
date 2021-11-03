/*
 * MIT License
 *
 * Copyright (c) 2018, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptastic.rssreader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssReader {

	private void removeBadDate(InputStream inputStream) throws IOException {
		inputStream.mark(2);
		var firstChar = inputStream.read();

		if (firstChar != 65279 && firstChar != 13 && firstChar != 10 && !Character.isWhitespace(firstChar)) {
			inputStream.reset();
		} else if (firstChar == 13 || Character.isWhitespace(firstChar)) {
			var secondChar = inputStream.read();

			if (secondChar != 10 && !Character.isWhitespace(secondChar)) {
				inputStream.reset();
				inputStream.read();
			}
		}
	}

	public List<Item> retrieveItems(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Zio Tobia 3.0");
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		log.debug("GET Response Status:: " + httpResponse.getStatusLine().getStatusCode());

		InputStreamReader isr = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader reader = new BufferedReader(isr);

		var inputStream = httpResponse.getEntity().getContent();

		if (httpResponse.getEntity().getContentEncoding() != null && "gzip".equals(httpResponse.getEntity().getContentEncoding().getValue())) {
			inputStream = new GZIPInputStream(inputStream);
		}

		inputStream = new BufferedInputStream(inputStream);

		removeBadDate(inputStream);
		var itemIterator = new RssItemIterator(inputStream, url);
		List<Item> items = StreamSupport.stream(Spliterators.spliteratorUnknownSize(itemIterator, Spliterator.ORDERED), false).collect(Collectors.toList());

		isr.close();
		reader.close();
		inputStream.close();
		httpResponse.close();
		httpClient.close();

		return items;

	}

}