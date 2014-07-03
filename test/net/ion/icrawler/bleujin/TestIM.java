package net.ion.icrawler.bleujin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.stream.JsonWriter;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;

public class TestIM extends TestCase {

	public void testIm() throws Exception {
		Realm realm = new RealmBuilder().setPassword(System.getProperty("pwd")).setPrincipal("bleujin").build();
		Request login = new Request("https://im.i-on.net/zeroboard/?s_url=/zeroboard/main.php").realm(realm);
		Site site = Site.create().sleepTime(50).loginRequest(login);

		PageProcessor processor = new PageProcessor() {
			private String urlPattern = "(" + "https://im.i-on.net/zeroboard/view.php?*".replace(".", "\\.").replace("*", "[^\"'#]*") + ")";

			@Override
			public void process(Page page) {
				List<Link> requests = page.getHtml().links().regex(urlPattern).targets();
				// add urls to fetch
				page.addTargets(requests);
				// extract by XPath
				page.putField("title", page.getHtml().xpath("//title"));
				page.putField("subject", page.getHtml().xpath("//td[@class='title_han']/b/text()"));
				// extract by Readability
				page.putField("content", page.getHtml().smartContent());
			}
		};
		
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10)).addUrl("https://im.i-on.net/zeroboard/main.php");

		final JWriter jwriter = new JWriter();
		spider.addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems ritems, Task task) {
				try {
					jwriter.write(ritems);
				} catch (IOException e) {
					e.printStackTrace();
				}

				Debug.line(ritems.getRequest().getQueryParameter());
			}
		}).run();
		jwriter.close();

		spider.close();
	}
}

class JWriter {

	private Map<String, JsonWriter> inner = MapUtil.newMap();

	public void write(ResultItems ritems) throws IOException {
		Map<String, String> qparam = ritems.getRequest().getQueryParameter();
		String id = qparam.get("id");
		if (StringUtil.isBlank(id))
			return;

		JsonWriter jwriter = inner.get(id);
		if (jwriter == null) {
			jwriter = new JsonWriter(new OutputStreamWriter(new FileOutputStream(new File("./resource/" + id + ".json")), "UTF-8"));
			inner.put(id, jwriter) ;
			jwriter.beginArray();
		}
		jwriter.beginObject();
		jwriter.name("url").value(ritems.getRequest().getUrl());
		jwriter.name("title").value(ritems.asString("title"));
		jwriter.name("subject").value(ritems.asString("subject"));

		jwriter.endObject();
		Debug.line(ritems.getRequest());
	}

	public void close() {
		for (JsonWriter jwriter : inner.values()) {
			try {
				jwriter.endArray();
			} catch (IOException ignore) { 
				ignore.printStackTrace(); 
			}
			IOUtil.closeQuietly(jwriter);
		}
	}

}