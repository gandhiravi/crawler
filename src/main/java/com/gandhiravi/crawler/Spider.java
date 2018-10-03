package com.gandhiravi.crawler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gandhiravi.crawler.model.Page;
import com.gandhiravi.crawler.util.Constant;
import com.gandhiravi.crawler.util.Utilities;

public class Spider {

	private static final Logger LOGGER = LogManager.getLogger(Spider.class);
	private SortedMap<String, Page> siteMap;
	private Utilities util;

	public Spider(URL url, boolean launch) throws MalformedURLException {
		util = new Utilities();
		LOGGER.info(url.toString());
		if (util.validateLink(url.toString())) {
			String baseUrl = url.getProtocol() + "://" + url.getHost();
			this.siteMap = new TreeMap<String, Page>(Comparator.comparing(String::toString));
			// Trigger for launching process -- useful for restricted while testing
			if (launch) {
				this.fetchUrl(baseUrl, url, 1);
			}
		} else {
			LOGGER.error("Invalid input URL - " + url);
		}
	}

	public final SortedMap<String, Page> getSiteMap() {
		return siteMap;
	}

	public final Page fetchUrl(String baseUrl, URL visitUrl, int depthLevel) throws MalformedURLException {
		if (this.siteMap.containsKey(visitUrl.getPath())) {
			return this.siteMap.get(visitUrl.getPath());
		} else {
			Page page = new Page(visitUrl);
			populateLinks(baseUrl, page, depthLevel);
			depthLevel++;
			for (String l : page.getInternalLinks().keySet()) {
				if (page.getInternalLinks().get(l).equals(true)) {
					Page p = fetchUrl(baseUrl, new URL(baseUrl + l), depthLevel);
					if (p != null) {
						this.siteMap.put(l, p);
					}
				}
			}
			// this.siteMap.put(baseUrl, page);
			this.siteMap.put(visitUrl.getPath(), page);
			return page;
		}
	}

	public final void populateLinks(String baseUrl, Page page, int depthLevel) {
		LOGGER.debug("populateLinks for - " + page);
		String host = page.getUrl().getHost();
		String path = page.getUrl().getPath();		
		try {
			Document doc = util.fetchDocument(page.getUrl().toString(), depthLevel);
			Elements selectLinks = doc.select("a");
			for (Element e : selectLinks) {
				String relUrl = e.attr("href");
				// Skip cases where tag is not pointing to any important link like Redirection
				// to Same page or Home Page
				if (relUrl.equals("/") || relUrl.startsWith("#") || relUrl.contains("Javascript")
						|| relUrl.contains("javascript")) { // Meta Tag or
															// JavaScript
															// call
					continue;
				}
				// External Links
				if (!(relUrl.contains(host) || relUrl.startsWith("/"))) {
					if (!relUrl.isEmpty()) {
						LOGGER.debug("external link - " + e.attr("href"));
						page.getExternalLinks().add(e.attr("href"));
					}
				} else {
					String absUrl = baseUrl + relUrl;
					if (relUrl.contains(Constant.FILE_DELIMITER)) {
						LOGGER.debug("static content - " + absUrl);
						page.getStaticContent().add(absUrl);
					} else {
						URL tempUrl = new URL(absUrl);
						// Parse all links on 1st depthLevel - Post that depth, parse only page related
						// links & not the same link
						if (depthLevel == 1 || relUrl.contains(path) && !(tempUrl.getPath().equals(path) || path.contains(relUrl))) {
							LOGGER.debug("internal link, with true flag for deep parsing - " + relUrl + "\t"
									+ absUrl);
							page.getInternalLinks().put(relUrl, true);
						} else {
							LOGGER.debug("internal link, but no deep parsing - " + relUrl + "\t" + absUrl);
							page.getInternalLinks().put(relUrl, false);
						}
					}

				}
			}
			Elements imgTags = doc.select("img");
			for (Element e : imgTags) {
				LOGGER.debug("static content - " + e.attr("src"));
				page.getStaticContent().add(e.attr("src"));
			}			
		} catch (IOException e) {
			LOGGER.catching(e);
			e.printStackTrace();
		}
	}
}
