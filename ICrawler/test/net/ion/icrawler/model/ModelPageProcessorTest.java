package net.ion.icrawler.model;

import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.model.ModelPageProcessor;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.TargetUrl;
import net.ion.icrawler.selector.PlainText;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * @date 14-4-4
 */
public class ModelPageProcessorTest {

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

	@Test
	public void testMultiModel_should_not_skip_when_match() throws Exception {
		Page page = new Page();
		page.setRawText("<div foo='foo'></div>");
		page.setRequest(new Request("http://codecraft.us/foo"));
		page.setUrl(PlainText.create("http://codecraft.us/foo"));
		ModelPageProcessor modelPageProcessor = ModelPageProcessor.create(null, ModelFoo.class, ModelBar.class);
		modelPageProcessor.process(page);
		assertThat(page.getResultItems().isSkip()).isFalse();

	}
}
