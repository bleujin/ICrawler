package net.ion.icrawler;

import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.collections.map.MultiValueMap;

import net.ion.framework.parse.gson.Gson;
import net.ion.framework.parse.gson.GsonBuilder;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

public class TestSpider extends TestCase {

	public void testFind404FromION() throws Exception {
		Site site = Site.create("http://www.i-on.net/index.html").sleepTime(50);

		String hostPattern = "http://www.i-on.net/*";
		final String urlPattern = (new StringBuilder("(")).append(hostPattern.replace(".", "\\.").replace("*", "[^\"'#]*")).append(")").toString();

		
		final MultiValueMap refs = new MultiValueMap() ;
		
		PageProcessor processor = new PageProcessor() {
			public void process(Page page) {
				List<Link> links = page.getHtml().links().regex(urlPattern).targets();
				
				
				JsonObject json = new JsonObject() ;
				json.put("status", page.getStatusCode()) ;
				json.put("url", page.getRequest().getUrl()) ;
				json.put("title", page.getHtml().xpath("//title/text()").get()) ;
				json.put("images", new JsonArray().adds(page.getHtml().xpath("//img/@src").all().toArray(new String[0]))) ;
				json.put("links", new JsonArray().adds(links.toArray(new Link[0])));
		
				for (Link link : links) {
					refs.put(link.target(), page.getRequest().getUrl()) ;
				}
				
				page.putField("result", json);
				page.addTargets(links);
			}
		};

		final Gson gson = new GsonBuilder().setPrettyPrinting().create() ;
		final StringWriter writer = new StringWriter(16000);
		Pipeline debug = new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				JsonObject json = ritems.asObject("result");
				json.add("refs", new JsonArray().adds(refs.getCollection(json.asString("url")).toArray()));
				if (json.asInt("status") == 404) gson.toJson(json, writer) ;
			}
		};

		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 1000));
		spider.addPipeline(debug).run();
		Debug.line(writer);
	}
}
