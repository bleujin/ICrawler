package net.ion.icrawler.example;

import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.monitor.SpiderMonitor;
import net.ion.icrawler.processor.example.GithubRepoPageProcessor;
import net.ion.icrawler.processor.example.TistoryBlogPageProcessor;

public class MonitorExample {

	public static void main(String[] args) throws Exception {

		Spider tistory = Site.create("http://bleujin.tistory.com").createSpider(new TistoryBlogPageProcessor()).setUUID("tistory");
		Spider githubSpider = Site.create("https://github.com/bleujin").createSpider(new GithubRepoPageProcessor()).setUUID("github");

		SpiderMonitor.instance().register(tistory);
		SpiderMonitor.instance().register(githubSpider);
		tistory.start();
		githubSpider.start();
	}
}
