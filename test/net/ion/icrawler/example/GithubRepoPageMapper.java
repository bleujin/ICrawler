package net.ion.icrawler.example;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.model.PageMapper;
import net.ion.icrawler.processor.PageProcessor;

public class GithubRepoPageMapper implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(0);

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

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new GithubRepoPageMapper()).addUrl("https://github.com/bleujin").thread(5).run();
	}
}