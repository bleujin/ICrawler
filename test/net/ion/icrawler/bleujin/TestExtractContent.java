package net.ion.icrawler.bleujin;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.icrawler.Page;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

public class TestExtractContent extends TestCase {

	public void testAPI() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50);
		
		Spider spider = site.newSpider(new PageProcessor() {
			private String urlPattern = "http://bleujin.tistory.com/*" ;
			@Override
			public void process(Page page) {
				List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
				page.addTargets(requests);// add urls to fetch
//				Debug.debug(requests.size(), requests);

				page.putField("title", page.getHtml().xpath("/title"));
				page.putField("html", page.getHtml().toString());
				page.putField("content", page.getHtmlContent().extractArticle()); // extract by Readability
			}
		});

		spider.setScheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				Debug.line(ritems.getRequest(), ritems.asString("title"), ritems.asString("content"));
			}
		}).run();
	}
}
