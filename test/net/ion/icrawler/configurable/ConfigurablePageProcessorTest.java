package net.ion.icrawler.configurable;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.configurable.ConfigurablePageProcessor;
import net.ion.icrawler.configurable.ExpressionType;
import net.ion.icrawler.configurable.ExtractRule;
import net.ion.icrawler.downloader.MockGithubDownloader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * @date 14-4-5
 */
public class ConfigurablePageProcessorTest {

	@Test
	public void test() throws Exception {
		List<ExtractRule> extractRules = new ArrayList<ExtractRule>();
		ExtractRule extractRule = new ExtractRule();
		extractRule.setExpressionType(ExpressionType.XPath);
		extractRule.setExpressionValue("//title");
		extractRule.setFieldName("title");
		extractRules.add(extractRule);
		extractRule = new ExtractRule();
		extractRule.setExpressionType(ExpressionType.XPath);
		extractRule.setExpressionValue("//ul[@class='pagehead-actions']/li[1]//a[@class='social-count js-social-count']/text()");
		extractRule.setFieldName("star");
		extractRules.add(extractRule);
		ResultItems resultItems = Site.create("").newSpider(new ConfigurablePageProcessor(extractRules)).setDownloader(new MockGithubDownloader()).get("https://github.com/code4craft/webmagic");
		assertThat(resultItems.getAll()).containsEntry("title", "<title>code4craft/webmagic &middot; GitHub</title>");
		assertThat(resultItems.getAll()).containsEntry("star", " 86 ");

	}
}
