package net.ion.icrawler.processor.example;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.processor.PageProcessor;

/**
 * <br>
 * 
 * @since 0.3.2
 */
public class GithubRepoPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).sleepTime(0);

	@Override
	public void process(Page page) {
		page.addRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
		page.addRequests(page.getHtml().links().regex("(https://github\\.com/\\w+)").all());
		page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
		if (page.getResultItems().asObject("name") == null) {
			// skip this page
			page.setSkip(true);
		}
		page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
	}


	public static void main(String[] args) {
		Site.me().setRetryTimes(3).sleepTime(0).createSpider(new GithubRepoPageProcessor()).addUrl("https://github.com/bleujin").thread(5).run();
	}
}
