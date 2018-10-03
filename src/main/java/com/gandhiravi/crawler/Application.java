package com.gandhiravi.crawler;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gandhiravi.crawler.util.Constant;
import com.gandhiravi.crawler.util.Utilities;

public class Application {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	public Application(String url, String outputFormat) {
		LOGGER.info("Website to Crawl : " + url);
		LOGGER.info("Output Format : " + outputFormat);
		Utilities util = new Utilities();
		if (util.validateLink(url)) {
			try {
				if (outputFormat.equalsIgnoreCase(Constant.OUTPUT_FORMAT_JSON)
						|| outputFormat.equalsIgnoreCase(Constant.OUTPUT_FORMAT_YAML)) {
					Spider spider = new Spider(new URL(url), true);
					util.exportOutput(spider.getSiteMap(), outputFormat);
				} else {
					LOGGER.error("Invalid format " + outputFormat + " in arguments");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.error("Invalid input URL - "+url);
		}
	}

	public static void main(String args[]) {
		if (args.length != 2) {
			LOGGER.error("Launch with 2 arguments - Website URL & Output Format (JSON or YAML) ..");			
		} else {
			new Application(args[0], args[1]);
		}
	}
}