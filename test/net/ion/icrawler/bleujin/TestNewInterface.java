package net.ion.icrawler.bleujin;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

public class TestNewInterface extends TestCase{

	public void testSimple() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50) ;
		
		Spider spider = site.createSpider(new SimplePageProcessor("http://bleujin.tistory.com/\\d+"))
				.startUrls("http://bleujin.tistory.com/").scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		
		spider.addPipeline(new DebugPipeline()).run();
	}
	
}
