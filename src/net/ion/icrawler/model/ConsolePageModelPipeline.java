package net.ion.icrawler.model;

import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.PageModelPipeline;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * <br>
 * 
 * @since 0.2.0
 */
public class ConsolePageModelPipeline implements PageModelPipeline {
	@Override
	public void process(Object o, Task task) {
		System.out.println(ToStringBuilder.reflectionToString(o));
	}
}
