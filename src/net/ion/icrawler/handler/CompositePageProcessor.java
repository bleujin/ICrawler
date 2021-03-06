package net.ion.icrawler.handler;

import java.util.ArrayList;
import java.util.List;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;
import net.ion.icrawler.processor.PageProcessor;

public class CompositePageProcessor implements PageProcessor {

	private List<SubPageProcessor> subPageProcessors = new ArrayList<SubPageProcessor>();

	public CompositePageProcessor() {
	}

	@Override
	public void process(Page page) {
		for (SubPageProcessor subPageProcessor : subPageProcessors) {
			if (subPageProcessor.match(page.getRequest())) {
				SubPageProcessor.MatchOther matchOtherProcessorProcessor = subPageProcessor.processPage(page);
				if (matchOtherProcessorProcessor == null || matchOtherProcessorProcessor != SubPageProcessor.MatchOther.YES) {
					return;
				}
			}
		}
	}
	public CompositePageProcessor addSubPageProcessor(SubPageProcessor subPageProcessor) {
		this.subPageProcessors.add(subPageProcessor);
		return this;
	}

	public CompositePageProcessor setSubPageProcessors(SubPageProcessor... subPageProcessors) {
		this.subPageProcessors = new ArrayList<SubPageProcessor>();
		for (SubPageProcessor subPageProcessor : subPageProcessors) {
			this.subPageProcessors.add(subPageProcessor);
		}
		return this;
	}

}
