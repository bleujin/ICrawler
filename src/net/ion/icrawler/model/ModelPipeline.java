package net.ion.icrawler.model;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.pipeline.Pipeline;

/**
 * The extension to Pipeline for page model extractor.
 */
class ModelPipeline implements Pipeline {

	private Map<Class, PageModelPipeline> pageModelPipelines = new ConcurrentHashMap<Class, PageModelPipeline>();

	public ModelPipeline() {
	}

	public ModelPipeline put(Class clazz, PageModelPipeline pageModelPipeline) {
		pageModelPipelines.put(clazz, pageModelPipeline);
		return this;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		try {
			for (Map.Entry<Class, PageModelPipeline> classPageModelPipelineEntry : pageModelPipelines.entrySet()) {
				Object o = resultItems.asObject(classPageModelPipelineEntry.getKey().getCanonicalName());
				if (o != null) {
					Annotation annotation = classPageModelPipelineEntry.getKey().getAnnotation(ExtractBy.class);
					if (annotation == null || !((ExtractBy) annotation).multi()) {
						classPageModelPipelineEntry.getValue().process(o, task);
					} else {
						List<Object> list = (List<Object>) o;
						for (Object o1 : list) {
							classPageModelPipelineEntry.getValue().process(o1, task);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
