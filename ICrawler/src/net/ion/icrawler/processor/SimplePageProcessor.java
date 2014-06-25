package net.ion.icrawler.processor;

import java.util.List;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.selector.Link;
import net.ion.icrawler.utils.UrlUtils;

/**
 * A simple PageProcessor.
 * 
 * <br>
 * 
 * @since 0.1.0
 */
public class SimplePageProcessor implements PageProcessor {

	private String urlPattern;

	private Site site;

	public SimplePageProcessor(String startUrl, String urlPattern) {
		this.site = Site.me().addStartUrl(startUrl).setDomain(UrlUtils.getDomain(startUrl));
		// compile "*" expression to regex
		this.urlPattern = "(" + urlPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";

	}

	@Override
	public void process(Page page) {
		List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
		// add urls to fetch
		page.addTargetTargets(requests);
		// extract by XPath
		page.putField("title", page.getHtml().xpath("//title"));
		page.putField("html", page.getHtml().toString());
		// extract by Readability
		page.putField("content", page.getHtml().smartContent());
	}

	@Override
	public Site getSite() {
		// settings
		return site;
	}
}
