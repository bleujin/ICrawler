package net.ion.icrawler.configurable;

import java.util.List;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.utils.Experimental;

/**
 * <br>
 */
@Experimental
public class ConfigurablePageProcessor implements PageProcessor {

	private Site site;

	private List<ExtractRule> extractRules;

	public ConfigurablePageProcessor(Site site, List<ExtractRule> extractRules) {
		this.site = site;
		this.extractRules = extractRules;
	}

	@Override
	public void process(Page page) {
		for (ExtractRule extractRule : extractRules) {
			if (extractRule.isMulti()) {
				List<String> results = page.getHtml().selectDocumentForList(extractRule.getSelector());
				if (extractRule.isNotNull() && results.size() == 0) {
					page.setSkip(true);
				} else {
					page.getResultItems().put(extractRule.getFieldName(), results);
				}
			} else {
				String result = page.getHtml().selectDocument(extractRule.getSelector());
				if (extractRule.isNotNull() && result == null) {
					page.setSkip(true);
				} else {
					page.getResultItems().put(extractRule.getFieldName(), result);
				}
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

}
