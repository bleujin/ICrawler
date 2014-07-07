package net.ion.icrawler.bleujin;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
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
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.NewClient.BoundRequestBuilder;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;

public class TestLogin extends TestCase {

	
	public void testLogin() throws Exception {
		NewClient nc = NewClient.create();
		Response response = nc.preparePost("https://www.tistory.com/auth/login")
				.addParameter("loginId", "bleujin@gmail.com")
				.addParameter("password", System.getProperty("pwd"))
				.addParameter("redirectUrl", "http://bleujin.tistory.com/")
				.execute().get() ;
		Debug.line(response.getStatusCode(), response.getTextBody(), response.getHeaders());
		
		List<Cookie> cookies = response.getCookies() ;
		BoundRequestBuilder rb = nc.prepareGet("http://bleujin.tistory.com/");
		for (Cookie c : cookies) {
			rb.addCookie(c) ;
		}
		
		response = rb.execute().get() ;
		String body = response.getTextBody() ;
		Debug.debug(StringUtil.substringAfter(body, "<!-- 포스트관리 -->").substring(1, 100)) ;
		
		nc.close() ;
	}
	
	public void testAfterLogin() throws Exception {
		
		Request login = new Request("https://www.tistory.com/auth/login")
			.addParameter("loginId", "bleujin@gmail.com")
			.addParameter("password", System.getProperty("pwd"))
			.addParameter("redirectUrl", "http://bleujin.tistory.com/").setMethod("POST");
		
		
		Site site = Site.create("http://www.tistory.com").sleepTime(50).loginRequest(login) ;
		
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
	
	
	public void testRealmAuthAtNewClient() throws Exception {
		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaximumConnectionsPerHost(1).build());
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword(System.getProperty("pwd")).build();
		RequestBuilder builder = new RequestBuilder();
		net.ion.radon.aclient.Request request = builder.setUrl("https://im.i-on.net/zeroboard/?s_url=/zeroboard/main.php").setRealm(realm).build() ;
		Response response = nc.prepareRequest(request).execute().get() ;
		debugResponse(response);

		List<Cookie> cookies = response.getCookies() ;
		BoundRequestBuilder rb = nc.prepareGet("https://im.i-on.net/zeroboard/main.php");
		for (Cookie c : cookies) {
			rb.addCookie(new Cookie(c.getDomain(), c.getName(), c.getValue(), c.getPath(), c.getMaxAge(), false)) ;
		}

		response = rb.execute().get() ;
		debugResponse(response);

		nc.close() ;
	}

	private void debugResponse(Response response) throws IOException {
		Debug.line(response.getStatusCode(), response.getTextBody("euc-kr"), response.getCookies());
	}
	
	public void testImAuth() throws Exception {
		Realm realm = new RealmBuilder().setPassword(System.getProperty("pwd")).setPrincipal("bleujin").build() ;
		Request login = new Request("https://im.i-on.net/zeroboard/?s_url=/zeroboard/main.php").realm(realm);
		Site site = Site.create("https://im.i-on.net/").sleepTime(50).loginRequest(login);
		

		SimplePageProcessor processor = new SimplePageProcessor("https://im.i-on.net/zeroboard/*");
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10)).addUrl("https://im.i-on.net/zeroboard/main.php");

		spider.addPipeline(new DebugPipeline()).run();
	}
	
}
