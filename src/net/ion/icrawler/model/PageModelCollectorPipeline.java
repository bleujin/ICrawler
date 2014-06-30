package net.ion.icrawler.model;

import java.lang.annotation.Annotation;
import java.util.List;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.pipeline.CollectorPageModelPipeline;
import net.ion.icrawler.pipeline.CollectorPipeline;

class PageModelCollectorPipeline<T> implements CollectorPipeline<T> {

	private final CollectorPageModelPipeline<T> classPipeline = new CollectorPageModelPipeline<T>();

	private final Class<?> clazz;

	PageModelCollectorPipeline(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public List<T> getCollected() {
		return classPipeline.getCollected();
	}

	@Override
	public synchronized void process(ResultItems resultItems, Task task) {
		Object o = resultItems.asObject(clazz.getCanonicalName());
		try {
			if (o != null) {
				Annotation annotation = clazz.getAnnotation(ExtractBy.class);
				if (annotation == null || !((ExtractBy) annotation).multi()) {
					classPipeline.process((T) o, task);
				} else {
					List<Object> list = (List<Object>) o;
					for (Object o1 : list) {
						classPipeline.process((T) o1, task);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}
	}
}
