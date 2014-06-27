package net.ion.icrawler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.selector.Selector;

/**
 * The extension to PageProcessor for page model extractor.
 * 
 * <br>
 * 
 * @since 0.2.0
 */
class ModelPageProcessor implements PageProcessor {

	private List<PageModelExtractor> pageModelExtractorList = new ArrayList<PageModelExtractor>();

	private Site site;

	public static ModelPageProcessor create(Site site, Class... clazzs) {
		ModelPageProcessor modelPageProcessor = new ModelPageProcessor(site);
		for (Class clazz : clazzs) {
			modelPageProcessor.addPageModel(clazz);
		}
		return modelPageProcessor;
	}

	public ModelPageProcessor addPageModel(Class clazz) {
		PageModelExtractor pageModelExtractor = PageModelExtractor.create(clazz);
		pageModelExtractorList.add(pageModelExtractor);
		return this;
	}

	private ModelPageProcessor(Site site) {
		this.site = site;
	}

	@Override
	public void process(Page page) {
		for (PageModelExtractor pageModelExtractor : pageModelExtractorList) {
			extractLinks(page, pageModelExtractor.getHelpUrlRegionSelector(), pageModelExtractor.getHelpUrlPatterns());
			extractLinks(page, pageModelExtractor.getTargetUrlRegionSelector(), pageModelExtractor.getTargetUrlPatterns());
			Object process = pageModelExtractor.process(page);
			if (process == null || (process instanceof List && ((List) process).size() == 0)) {
				continue;
			}
			postProcessPageModel(pageModelExtractor.getClazz(), process);
			page.putField(pageModelExtractor.getClazz().getCanonicalName(), process);
		}
		if (page.getResultItems().getAll().size() == 0) {
			page.getResultItems().setSkip(true);
		}
	}

	private void extractLinks(Page page, Selector urlRegionSelector, List<Pattern> urlPatterns) {
		List<String> links;
		if (urlRegionSelector == null) {
			links = page.getHtml().links().all();
		} else {
			links = page.getHtml().selectList(urlRegionSelector).links().all();
		}
		for (String link : links) {
			for (Pattern targetUrlPattern : urlPatterns) {
				Matcher matcher = targetUrlPattern.matcher(link);
				if (matcher.find()) {
					page.addTarget(new Request(matcher.group(1), page.getRequest()));
				}
			}
		}
	}

	protected void postProcessPageModel(Class clazz, Object object) {
	}

	@Override
	public Site getSite() {
		return site;
	}
}
