package net.ion.icrawler.example;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.model.PageMapper;
import net.ion.icrawler.processor.PageProcessor;

public class GithubRepoPageMapper implements PageProcessor {

	private PageMapper<GithubRepo> githubRepoPageMapper = new PageMapper<GithubRepo>(GithubRepo.class);
	@Override
	public void process(Page page) {
		page.addRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
		page.addRequests(page.getHtml().links().regex("(https://github\\.com/\\w+)").all());
		GithubRepo githubRepo = githubRepoPageMapper.get(page);
		if (githubRepo == null) {
			page.setSkip(true);
		} else {
			page.putField("repo", githubRepo);
		}

	}


	public static void main(String[] args) {
		Site.create("https://github.com/bleujin").setRetryTimes(3).sleepTime(0).newSpider(new GithubRepoPageMapper()).thread(5).run();
	}
}