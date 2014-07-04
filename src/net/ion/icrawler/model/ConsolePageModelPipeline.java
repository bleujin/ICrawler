package net.ion.icrawler.model;

import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.PageModelPipeline;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 */
public class ConsolePageModelPipeline implements PageModelPipeline {
	@Override
	public void process(Object o, Task task) {
		System.out.println(ToStringBuilder.reflectionToString(o));
	}
}
