package net.ion.icrawler.configurable;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.downloader.MockGithubDownloader;


public class ConfigurablePageProcessorTest extends TestCase{

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
		assertEquals("<title>code4craft/webmagic &middot; GitHub</title>", resultItems.asObject("title")) ;
		assertEquals(" 86 ", resultItems.asString("star"));
	}
}
