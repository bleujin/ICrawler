package net.ion.icrawler.bleujin;

import junit.framework.TestCase;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

public class TestDaumLogin extends TestCase {

	public void testAfterLogin() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Spider spider = Site.create("").sleepTime(50).createSpider(processor)
				.scheduler(new MaxLimitScheduler(new QueueScheduler(), 100))
				.startUrls("http://bleujin.tistory.com/");

		spider.addPipeline(new DebugPipeline()).run();
	}
	
	
}
