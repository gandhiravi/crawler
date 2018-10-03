package com.gandhiravi.crawler.model;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * @author Ravi Gandhi POJO to hold link - details scrapped on a particular
 *         link.
 *
 */
public class Page implements Comparable<Page> {

	final private URL url; // URL of page to be crawled
	final private Set<String> staticContent, externalLinks; // Sets to hold unique static content and external host
															// links
	final private SortedMap<String, Boolean> internalLinks; // Map to hold internal relative links with flag to perform
															// depth crawling

	public Page(URL url) {
		this.url = url;
		staticContent = new HashSet<String>();
		internalLinks = new TreeMap<String, Boolean>(Comparator.comparing(String::toString));
		externalLinks = new HashSet<String>();
	}

	public final Set<String> getStaticContent() {
		return staticContent;
	}

	public final Map<String, Boolean> getInternalLinks() {
		return internalLinks;
	}

	
	public final Set<String> getExternalLinks() {
		return externalLinks;
	}

	@Override
	public String toString() {
		return "Page [url=" + url + ", staticContent=" + staticContent + ", externalLinks=" + externalLinks
				+ ", internalLinks=" + internalLinks + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Page other = (Page) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	public URL getUrl() {
		return url;
	}

	@Override
	public int compareTo(Page o) {
		return this.url.toString().compareTo(o.getUrl().toString());
	}

}
