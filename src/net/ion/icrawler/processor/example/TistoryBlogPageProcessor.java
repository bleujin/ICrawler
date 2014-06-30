package net.ion.icrawler.processor.example;

import java.util.List;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.processor.PageProcessor;

/**
 * <br>
 */
public class TistoryBlogPageProcessor implements PageProcessor {

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().links().regex("http://bleujin\\.tistory\\.com/\\d+").all();
		page.addRequests(links);
		page.putField("title", page.getHtml().xpath("//h2[@class='title']/a/text()").toString());
		if (page.getResultItems().asObject("title") == null) {
			// skip this page
			page.setSkip(true);
		}
		page.putField("content", page.getHtml().smartContent().toString());
		page.putField("tags", page.getHtml().xpath("//div[@class='tagTrail']/a/text()").all());
	}


	public static void main(String[] args) throws Exception{
		Site.create("bleujin.tistory.com").newSpider(new TistoryBlogPageProcessor()).addUrl("http://bleujin.tistory.com").run();
	}
}
