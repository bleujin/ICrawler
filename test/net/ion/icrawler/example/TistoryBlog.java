package net.ion.icrawler.example;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.Formatter;
import net.ion.icrawler.model.annotation.TargetUrl;
import net.ion.icrawler.pipeline.JsonFilePageModelPipeline;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

@TargetUrl("http://bleujin.tistory.com/\\d+")
public class TistoryBlog extends TestCase{

	@ExtractBy(value = "//h2[@class='title']/a/text()")
	private String title;

	@ExtractBy(value = "div.article", type = ExtractBy.Type.Css)
	private String content;

	@ExtractBy(value = "//div[@class='tagTrail']/a/text()", multi = true)
	private List<String> tags;

	@ExtractBy("//div[@class='infor']//span[@class='date']/regex('\\d+\\/\\d+\\/\\d+\\s+\\d+:\\d+')")
	private String date; // 

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public List<String> getTags() {
		return tags;
	}

	public Date getDate() {
		return DateUtil.stringToDate(date, "yyyy/MM/dd HH:mm");
	}

	
	public void testRun() throws Exception {
		// results will be saved to "/data/webmagic/" in json format
		Spider spider = Site.create().createOOSpider(new PageModelPipeline<TistoryBlog>(){

			@Override
			public void process(TistoryBlog t, Task task) {
				Debug.line(t.title, t.getDate(), t.tags);
			}
		}, TistoryBlog.class).addUrl("http://bleujin.tistory.com");
		spider.setScheduler(new MaxLimitScheduler(new QueueScheduler(), 10)) ;
		spider.getSite().sleepTime(50) ;
		spider.run();
	}

}
