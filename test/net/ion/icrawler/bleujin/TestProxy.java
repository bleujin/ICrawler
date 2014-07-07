package net.ion.icrawler.bleujin;

import junit.framework.TestCase;
import net.ion.framework.util.ListUtil;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.proxy.HttpHost;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestProxy extends TestCase {

	public void testNoProxy() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Spider spider = Site.create("http://bleujin.tistory.com").sleepTime(50).newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));

		spider.addPipeline(new DebugPipeline()).run();
	}

	public void testUseProxyAtJsoup() throws Exception {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "8888");

		Document doc = Jsoup.connect("http://display.ad.daum.net/imp?slotid=0K905").get();
		System.out.println("content " + doc.html());
	}


	public void testWithProxy() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com/163").sleepTime(50)
					.setHttpProxy(HttpHost.createByHost("127.0.0.1", 8888)).setHttpProxyPool(ListUtil.create(new String[]{"127.0.0.1", "8888"})); // used fiddler

		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/\\d+");
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.addPipeline(new DebugPipeline()).run();
	}

}
