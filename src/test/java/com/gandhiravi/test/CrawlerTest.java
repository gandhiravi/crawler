package com.gandhiravi.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.gandhiravi.crawler.Spider;
import com.gandhiravi.crawler.model.Page;
import com.gandhiravi.crawler.util.Constant;
import com.gandhiravi.crawler.util.Utilities;

public class CrawlerTest {
	private static final Logger LOGGER = LogManager.getLogger(CrawlerTest.class);

	@Test
	public void urlCheck() {
		LOGGER.info("Running test - urlCheck");
		String url1 = "https://www.prudential.co.uk";
		String url2 = "www.prudential.co.uk";
		String url3 = "https://www.prudential.co.uk/businesses/";
		Utilities util = new Utilities();

		assertTrue(new Boolean(util.validateLink(url1)).equals(new Boolean(true)));
		assertTrue(new Boolean(util.validateLink(url2)).equals(new Boolean(false)));
		assertTrue(new Boolean(util.validateLink(url3)).equals(new Boolean(true)));

	}

	@Test
	public void validateLinks() {
		LOGGER.info("Running test - ValidateLinks");	
		String base = "https://www.prudential.co.uk";		
		try {
			URL testUrl = new URL(base + "/businesses/asia");
			LOGGER.info("Test URL "+testUrl);
			Spider spider = new Spider(testUrl, false);
			Page page = new Page(testUrl);			
			spider.populateLinks(base, page, 2);
			spider.getSiteMap().put(testUrl.getPath(), page);			
			LOGGER.info("Check External links "+testUrl);
			assertTrue(page.getExternalLinks().size()==2);
			assertTrue(page.getExternalLinks().contains("http://www.prudentialcorporation-asia.com"));
			LOGGER.info("Check Internal links "+testUrl);
			assertTrue(page.getInternalLinks().size()==2);
			assertTrue(page.getInternalLinks().containsKey("/insights/the-role-of-insurance-in-asean-markets"));
			LOGGER.info("Check static links "+testUrl);
			assertTrue(page.getStaticContent().size()==4);
		}catch(MalformedURLException e) {
			LOGGER.catching(e);
		}
	}

}