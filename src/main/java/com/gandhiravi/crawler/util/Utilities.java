package com.gandhiravi.crawler.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.gandhiravi.crawler.model.Page;

public class Utilities {

	private static final Logger LOGGER = LogManager.getLogger(Utilities.class);

	UrlValidator urlValidator;

	public boolean validateLink(String link) {
		LOGGER.info("Validating Link : " + link);
		if (urlValidator == null) {
			urlValidator = new UrlValidator();
		}
		return urlValidator.isValid(link);
	}

	public Document fetchDocument(String url, int depthLevel) throws IOException {
		LOGGER.info("Fetching document for Link : " + url);
		if (validateLink(url)) {
			Document baseDoc = Jsoup.connect(url).get();
			baseDoc = Jsoup.parse(baseDoc.getElementById("MainWrapper").html());
			if (depthLevel != 1) {
				baseDoc.getElementsByClass("footer-main-area").get(0).remove();
				for (Element x : baseDoc.getElementsByClass("header")) {
					x.remove();
				}
				for (Element x : baseDoc.getElementsByClass("footer-main-area")) {
					x.remove();
				}

			}
			return baseDoc;
		}
		return null;

	}

	public String exportOutput(Map<String, Page> siteMap, String outputFormat) {
		LOGGER.info("Exporting in format - " + outputFormat);
		String filePath = null;
		try {
			if (outputFormat.equalsIgnoreCase(Constant.OUTPUT_FORMAT_YAML)) {
				filePath = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + Constant.FILE_DELIMITER
						+ Constant.OUTPUT_FORMAT_YAML;
				YAMLFactory yf = new YAMLFactory();
				ObjectMapper mapper = new ObjectMapper(yf);
				FileOutputStream fos = new FileOutputStream(filePath);
				YAMLGenerator yamlGen = yf.createGenerator(fos, JsonEncoding.UTF8);
				yamlGen.setPrettyPrinter(new DefaultPrettyPrinter());
				yamlGen.writeObject(siteMap);
			}
			if (outputFormat.equalsIgnoreCase(Constant.OUTPUT_FORMAT_JSON)) {
				filePath = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + Constant.FILE_DELIMITER
						+ Constant.OUTPUT_FORMAT_JSON;
				JsonFactory jf = new JsonFactory();
				ObjectMapper mapper = new ObjectMapper(jf);
				FileOutputStream fos = new FileOutputStream(filePath);
				JsonGenerator jsonGen = jf.createGenerator(fos, JsonEncoding.UTF8);
				jsonGen.setPrettyPrinter(new DefaultPrettyPrinter());
				jsonGen.writeObject(siteMap);
			}			
			if (filePath == null) {
				LOGGER.info("Unsupported Output format - " + outputFormat);
			}else{
				LOGGER.info("Details Exported to file - " + filePath);
			}
		} catch (IOException e) {
			LOGGER.catching(e);
			e.printStackTrace();
		}
		return filePath;
	}
}
