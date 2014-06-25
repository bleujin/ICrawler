package net.ion.icrawler.model;

import junit.framework.Assert;
import net.ion.icrawler.Site;
import net.ion.icrawler.Task;
import net.ion.icrawler.downloader.MockGithubDownloader;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.pipeline.PageModelPipeline;

import org.junit.Test;

/**
 * <br>
 */
public class GithubRepoTest {

	@Test
	public void test() {
		OOSpider.create(Site.me().addStartUrl("https://github.com/bleujin/ICrawler").setSleepTime(0), new PageModelPipeline<GithubRepo>() {
			@Override
			public void process(GithubRepo o, Task task) {
				Assert.assertEquals(86, o.getStar());
				Assert.assertEquals(70, o.getFork());
			}
		}, GithubRepo.class).setDownloader(new MockGithubDownloader()).test("https://github.com/bleujin/ICrawler");
	}
}
