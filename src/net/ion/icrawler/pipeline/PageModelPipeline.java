package net.ion.icrawler.pipeline;

import net.ion.icrawler.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 * 
 * <br>
 * 
 * @since 0.2.0
 */
public interface PageModelPipeline<T> {

	public void process(T t, Task task);

}
