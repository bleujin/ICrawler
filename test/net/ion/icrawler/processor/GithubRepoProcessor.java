package net.ion.icrawler.processor;

import junit.framework.Assert;
import net.ion.icrawler.*;
import net.ion.icrawler.downloader.MockGithubDownloader;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;

import org.junit.Test;

/**

 */
public class GithubRepoProcessor implements PageProcessor {
	@Override
	public void process(Page page) {
		page.putField("star", page.getHtml().xpath("//ul[@class='pagehead-actions']/li[2]//a[@class='social-count js-social-count']/text()").toString());
		page.putField("fork", page.getHtml().xpath("//ul[@class='pagehead-actions']/li[3]//a[@class='social-count']/text()").toString());
	}

	@Test
	public void test() {
		
		Site.create("https://github.com/code4craft/webmagic").createSpider(new GithubRepoProcessor()).addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				Assert.assertEquals("78", ((String) resultItems.asObject("star")).trim());
				Assert.assertEquals("65", ((String) resultItems.asObject("fork")).trim());
			}
		}).setDownloader(new MockGithubDownloader()).test("https://github.com/code4craft/webmagic");
	}

}
