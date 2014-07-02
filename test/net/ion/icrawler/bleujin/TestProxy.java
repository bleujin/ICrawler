package net.ion.icrawler.bleujin;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

	public void testUseProxyAtApacheCommon() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpHost target = new HttpHost("display.ad.daum.net", 80, "http");

			RequestConfig config = RequestConfig.custom().setProxy(new HttpHost("127.0.0.1", 8888, "http")).build();
			HttpGet request = new HttpGet("/imp?slotid=0K905");
			request.setConfig(config);
			
			System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + config.getProxy());

			CloseableHttpResponse response = httpclient.execute(target, request);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
//				InputStream content = entity.getContent() ;
//				String body = IOUtil.toStringWithClose(content) ;
//				System.out.println(body);
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	public void testWithProxy() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com/").sleepTime(50)
					.setHttpProxy(new HttpHost("127.0.0.1", 8888)).setHttpProxyPool(ListUtil.create(new String[]{"127.0.0.1", "8888"})); // used fiddler

		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/\\d+");
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10)).addUrl("http://bleujin.tistory.com");
		spider.addPipeline(new DebugPipeline()).run();
	}

}
