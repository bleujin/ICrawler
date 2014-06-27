package net.ion.icrawler.monitor;

import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.monitor.SpiderMonitor;
import net.ion.icrawler.monitor.SpiderStatusMXBean;
import net.ion.icrawler.processor.example.GithubRepoPageProcessor;
import net.ion.icrawler.processor.example.TistoryBlogPageProcessor;

import org.junit.Test;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class SpiderMonitorTest {

	@Test
	public void testInherit() throws Exception {
		SpiderMonitor spiderMonitor = new SpiderMonitor() {
			@Override
			protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener) {
				return new CustomSpiderStatus(spider, monitorSpiderListener);
			}
		};

		Spider oschinaSpider = Site.test().createSpider(new TistoryBlogPageProcessor()).addUrl("http://my.oschina.net/flashsword/blog").thread(2).setUUID("blog");
		Spider githubSpider = Site.create("https://github.com/code4craft").createSpider(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft").setUUID("github");

		spiderMonitor.register(oschinaSpider, githubSpider);

	}
}
