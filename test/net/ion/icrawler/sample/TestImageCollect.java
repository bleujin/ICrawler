package net.ion.icrawler.sample;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.icrawler.Page;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.selector.Link;

public class TestImageCollect extends TestCase {

	public void testCollectImage() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(20).setTimeOut(10*1000);

		ImageCollectorProcessor processor = new ImageCollectorProcessor("http://bleujin.tistory.com/*");
		Spider spider = site.newSpider(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 15));

		spider.addPipeline(new BlankPipeline()).run();
		
		Vector<String> images = processor.images() ;
		for (String img : images) {
			Debug.debug(img);
		}
		
		
	}
}

class ImageCollectorProcessor implements PageProcessor {
	private String urlPattern;
	private Vector<String> images = new Vector<String>() ;
	public ImageCollectorProcessor(String urlPattern) {
		this.urlPattern = "(" + urlPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";
	}

	@Override
	public void process(Page page) {
		List<String> found = page.getHtml().xpath("//img/@src").all();
		
		found.removeAll(images) ;
		boolean changed = images.addAll(found) ;
		
		page.addTargets(page.getHtml().links().regex(urlPattern).targets());// add urls to fetch
		page.putField("changed", changed);
		page.putField("found", found);
	}
	
	public Vector<String> images(){
		return images ;
	}
}

class BlankPipeline implements Pipeline {

	@Override
	public void process(ResultItems ritems, Task task) {
		if ("true".equals(ritems.asString("changed"))){
			
			Debug.line(ritems.getRequest().getUrl(), "new image founded", ritems.asObject("found"));
		}
	}

}
