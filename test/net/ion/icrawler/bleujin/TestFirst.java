package net.ion.icrawler.bleujin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.org.mozilla.javascript.internal.NativeObject;
import junit.framework.TestCase;
import net.ion.framework.parse.gson.stream.JsonWriter;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.icrawler.Page;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.example.TistoryBlog;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

import com.google.common.net.HttpHeaders;

public class TestFirst extends TestCase {

	public void testSimple() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com/*");
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50);
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));

		spider.addPipeline(new DebugPipeline()).run();
	}


	public void testModelWithScript() throws Exception {
		Spider spider = Site.create().sleepTime(50).createOOSpider(new PageModelPipeline<TistoryBlog>() {
			@Override
			public void process(TistoryBlog t, Task task) {
				Debug.line(t.getTitle(), t.getDate(), t.getTags());
			}
		}, TistoryBlog.class).addUrl("http://bleujin.tistory.com");
		spider.setScheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.run();
		
		
	}

	
	
	public void testViewJsoupDocument() throws Exception {
		PageProcessor processor = new PageProcessor() {
			public void process(Page page) {
				Document doc = page.getHtml().getDocument();
				Elements links = doc.select("a[href]");

				List<Link> targets = ListUtil.newList();
				for (Element link : links) {
					System.out.println(String.format("* %s : %s", link.attr("abs:href"), link.ownText()));
					targets.add(new Link(link.attr("abs:href"), link.ownText()));
				}

				page.addTargets(targets);
				if (page.getRequest().getUrl().endsWith("/"))
					page.setSkip(true);
			}
		};

		Spider spider = Site.create("http://bleujin.tistory.com/").newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.getSite().sleepTime(50);
		spider.run();

	}

	public void testJsonFilePipeline() throws Exception {
		PageProcessor processor = new PageProcessor() {
			private String urlPattern = "(" + "http://bleujin.tistory.com/\\d+".replace(".", "\\.").replace("*", "[^\"'#]*") + ")";
			public void process(Page page) {
				List<Link> requests = page.getHtml().links().regex(urlPattern).targets();

				page.addTargets(requests); // add urls to fetch
				page.putField("title", page.getHtml().xpath("//title"));
				page.putField("subject", page.getHtml().xpath("//h2[@class='title']/a/text()")); // extract by XPath
				page.putField("date", page.getHtml().xpath("//div[@class='infor']//span[@class='date']/regex('\\d+\\/\\d+\\/\\d+\\s+\\d+:\\d+')"));
				page.putField("content", page.getHtml().smartContent()); // extract by Readability
				if (page.getRequest().getUrl().endsWith("/"))
					page.setSkip(true);
			}
		};

		Spider spider = Site.create("http://bleujin.tistory.com/").newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.getSite().sleepTime(50);

		File file = new File("./resource/temp/tistory.json");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		final JsonWriter jwriter = new JsonWriter(new FileWriter(file));
		jwriter.setIndent("\t");
		jwriter.beginArray();

		Pipeline jsonFilePipeline = new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				try {

					jwriter.beginObject();
					jwriter.name("link").value(resultItems.getRequest().getUrl());
					jwriter.name("referer").value(resultItems.getRequest().asString(HttpHeaders.REFERER));
					jwriter.name("anchor").value(resultItems.getRequest().asString("Anchor"));
					jwriter.name("title").value(resultItems.asString("title"));
					jwriter.name("subject").value(resultItems.asString("subject"));
					jwriter.name("date").value(resultItems.asString("date"));
					jwriter.name("content").value(resultItems.asString("content"));
					jwriter.endObject();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		spider.addPipeline(jsonFilePipeline).run();
		jwriter.endArray();
		jwriter.flush();
		jwriter.close();

		spider.close();

	}
}
