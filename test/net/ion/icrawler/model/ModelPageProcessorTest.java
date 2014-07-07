package net.ion.icrawler.model;

import junit.framework.TestCase;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.TargetUrl;
import net.ion.icrawler.selector.PlainText;

import org.junit.Assert;
import org.junit.Test;

public class ModelPageProcessorTest extends TestCase{

	@TargetUrl("http://codecraft.us/foo")
	public static class ModelFoo {

		@ExtractBy(value = "//div/@foo", notNull = true)
		private String foo;

	}

	@TargetUrl("http://codecraft.us/bar")
	public static class ModelBar {

		@ExtractBy(value = "//div/@bar", notNull = true)
		private String bar;

	}

	public void testMultiModel_should_not_skip_when_match() throws Exception {
		Page page = new Page();
		page.setRawText("<div foo='foo'></div>");
		page.setRequest(new Request("http://codecraft.us/foo"));
		page.setUrl(PlainText.create("http://codecraft.us/foo"));
		ModelPageProcessor modelPageProcessor = ModelPageProcessor.create(ModelFoo.class, ModelBar.class);
		modelPageProcessor.process(page);
		assertFalse(page.getResultItems().isSkip());
	}
}
