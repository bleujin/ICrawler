package net.ion.icrawler.bleujin;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.BinaryHandler;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

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
					public String handle(Request request, Map headers, InputStream input) {
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
	
	public void testFindAllImgSrc() throws Exception {
		PageProcessor processor = new PageProcessor() {
			private String urlPattern = "(" + "http://bleujin.tistory.com/*".replace(".", "\\.").replace("*", "[^\"'#]*") + ")";
			@Override
			public void process(Page page) {
				List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
				page.addTargets(requests);
				
				List<String> imgLinks = page.getHtml().xpath("//img/@src").all() ;
				page.putField("title", page.getHtml().xpath("//title"));
				page.putField("imgs", imgLinks);
			}
		};
		
		
		final Set<String> imgs = SetUtil.newOrdereddSet() ;
		Spider spider = Site.create().sleepTime(10)
				.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 100)).addUrl("http://bleujin.tistory.com/");

		spider.addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				List<String> list = ritems.asObject("imgs") ;
				imgs.addAll(list) ;
			}
		}).run();
		
		for (String imgLink : imgs) {
			Debug.line(imgLink);
		}
	}
}
