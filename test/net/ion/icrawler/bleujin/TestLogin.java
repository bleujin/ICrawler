package net.ion.icrawler.bleujin;

import java.util.List;

import org.restlet.data.Method;

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
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class TestLogin extends TestCase {

	
	public void testLogin() throws Exception {
		NewClient nc = NewClient.create();
		Response response = nc.preparePost("https://www.tistory.com/auth/login")
				.addParameter("loginId", "bleujin@gmail.com")
				.addParameter("password", "redfpark")
				.addParameter("redirectUrl", "http://bleujin.tistory.com/")
				.execute().get() ;
		Debug.line(response.getStatusCode(), response.getTextBody(), response.getHeaders());
		nc.close() ;
	}
	
	public void testAfterLogin() throws Exception {
		
		Request login = new Request("https://www.tistory.com/auth/login")
			.addParameter("loginId", "bleujin@gmail.com")
			.addParameter("password", "redfpark")
			.addParameter("redirectUrl", "http://bleujin.tistory.com/").setMethod("POST");
		
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50).loginRequest(login) ;
		
		PageProcessor processor = new PageProcessor(){
				private String urlPattern = "(" + "http://bleujin.tistory.com/\\d+".replace(".", "\\.").replace("*", "[^\"'#]*") + ")";

				@Override
				public void process(Page page) {
					List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
					page.addTargets(requests);
					page.putField("title", page.getHtml().xpath("//h2[@class='title']/a/text()"));
					page.putField("admin", page.getHtml().xpath("//div[@class='admin']/text()")); // "//div[@class='tagTrail']/a/text()"
					page.putField("html", page.getHtml().toString());
					page.putField("content", page.getHtml().xpath("//div[@class='container']"));
				}			
		};
		Spider spider = site.newSpider(processor).startUrls("http://bleujin.tistory.com/") .scheduler(new MaxLimitScheduler(new QueueScheduler(), 5));

		spider.addPipeline(new Pipeline(){
			@Override
			public void process(ResultItems ritems, Task task) {
				Debug.line(ritems.asString("admin"), ritems.asString("title"));
			}}).run();
	}
	
}
