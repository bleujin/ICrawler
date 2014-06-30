package net.ion.icrawler.model;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.CollectorPipeline;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.utils.UrlUtils;

/**
 * The spider for page model extractor.<br>
 * In icrawler, we call a POJO containing extract result as "page model". <br>
 * You can customize a crawler by write a page model with annotations. <br>
 */
public class OOSpider<T> extends Spider {

	private ModelPageProcessor modelPageProcessor;

	private ModelPipeline modelPipeline;

	private PageModelPipeline pageModelPipeline;

	private List<Class> pageModelClasses = new ArrayList<Class>();

	protected OOSpider(Site site, ModelPageProcessor modelPageProcessor) {
		super(site, modelPageProcessor);
		this.modelPageProcessor = modelPageProcessor;
	}

	public OOSpider(Site site, PageProcessor pageProcessor) {
		super(site, pageProcessor);
	}

	public OOSpider(Site site, PageModelPipeline pageModelPipeline, Class... pageModels) {
		this(site, ModelPageProcessor.create(pageModels));
		this.modelPipeline = new ModelPipeline();
		super.addPipeline(modelPipeline);
		for (Class pageModel : pageModels) {
			if (pageModelPipeline != null) {
				this.modelPipeline.put(pageModel, pageModelPipeline);
			}
			pageModelClasses.add(pageModel);
		}
	}

	public OOSpider startUrls(String... startUrls) {
		super.startUrls(startUrls) ;
		return this;
	}
	

	public static OOSpider create(Site site, Class... pageModels) {
		return new OOSpider(site, null, pageModels);
	}

	public static OOSpider create(Site site, PageModelPipeline pageModelPipeline, Class... pageModels) {
		return new OOSpider(site, pageModelPipeline, pageModels);
	}

	@Override
	protected CollectorPipeline getCollectorPipeline() {
		return new PageModelCollectorPipeline<T>(pageModelClasses.get(0));
	}

	public OOSpider addPageModel(PageModelPipeline pageModelPipeline, Class... pageModels) {
		for (Class pageModel : pageModels) {
			modelPageProcessor.addPageModel(pageModel);
			modelPipeline.put(pageModel, pageModelPipeline);
		}
		return this;
	}

}
