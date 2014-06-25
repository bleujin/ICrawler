package net.ion.icrawler.example;

import net.ion.icrawler.Spider;
import net.ion.icrawler.monitor.SpiderMonitor;
import net.ion.icrawler.processor.example.GithubRepoPageProcessor;
import net.ion.icrawler.processor.example.TistoryBlogPageProcessor;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class MonitorExample {

	public static void main(String[] args) throws Exception {

		Spider oschinaSpider = Spider.create(new TistoryBlogPageProcessor()).addUrl("http://bleujin.tistory.com");
		Spider githubSpider = Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/bleujin");

		SpiderMonitor.instance().register(oschinaSpider);
		SpiderMonitor.instance().register(githubSpider);
		oschinaSpider.start();
		githubSpider.start();
	}
}
