package net.ion.icrawler.bleujin;

import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import junit.framework.TestCase;

public class TestCancel extends TestCase{

	public void testSimple() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(200);
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));

		spider.addPipeline(new DebugPipeline()).runAsync();
		
		Thread.sleep(2200);
		
		spider.stop();
		Thread.sleep(1000);
	}

}
