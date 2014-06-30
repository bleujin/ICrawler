package net.ion.icrawler.bleujin;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.example.TistoryBlog;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

public class TestNewInterface extends TestCase {

	public void testSimple() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50);

		Spider spider = site.newSpider(new SimplePageProcessor("http://bleujin.tistory.com/\\d+"))
				.scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));

		spider.addPipeline(new DebugPipeline()).run();
	}

	
	
	public void testOOSpider() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50);

		Spider spider = site.createOOSpider(new PageModelPipeline<TistoryBlog>() {
			@Override
			public void process(TistoryBlog t, Task task) {
				Debug.line(t.getTitle(), t.getDate(), t.getTags());
			}
		}, TistoryBlog.class)
			.setScheduler(new MaxLimitScheduler(new QueueScheduler(), 10))
			.addUrl("http://bleujin.tistory.com");
		
		spider.run();
	}

}

