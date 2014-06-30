package net.ion.icrawler.bleujin;

import java.io.InputStream;

import org.apache.commons.collections.map.MultiValueMap;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.icrawler.Request;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.BinaryHandler;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

public class TestBinaryHandling extends TestCase{
	
	
	public void testNoHandle() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Spider spider = Site.create().sleepTime(10)
				.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 30)).addUrl("http://bleujin.tistory.com/153", "http://bleujin.tistory.com/");

		spider.addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				String urlName = ritems.asString(BinaryHandler.class.getCanonicalName());
				Debug.line(ritems.getRequest().getUrl(), urlName);
			}
		}).run();
	}
	
	public void testHandleBinary() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Spider spider = Site.create().sleepTime(10)
				.binaryHandler(new BinaryHandler<String>() {
					@Override
					public String handle(Request request, MultiValueMap headers, InputStream input) {
						return request.getUrl();
					}
				})
				.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 30)).addUrl("http://bleujin.tistory.com/153", "http://bleujin.tistory.com/");

		spider.addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				String urlName = ritems.asString(BinaryHandler.class.getCanonicalName());
				Debug.line(ritems.getRequest().getUrl(), urlName);
			}
		}).run();
	}
	
}
