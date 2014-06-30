package net.ion.icrawler.pipeline;

import net.ion.icrawler.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 */
public interface PageModelPipeline<T> {

	public void process(T t, Task task);

}
