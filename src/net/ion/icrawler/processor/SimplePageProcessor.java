package net.ion.icrawler.processor;

import java.util.List;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.selector.Link;
import net.ion.icrawler.utils.UrlUtils;

public class SimplePageProcessor implements PageProcessor {

	private String urlPattern;
	public SimplePageProcessor(String urlPattern) {
		this.urlPattern = "(" + urlPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";

	}

	@Override
	public void process(Page page) {
		List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
		// add urls to fetch
		page.addTargets(requests);
		// extract by XPath
		page.putField("title", page.getHtml().xpath("//title"));
		page.putField("html", page.getHtml().toString());
		// extract by Readability
		page.putField("content", page.getHtml().smartContent());
	}
}
