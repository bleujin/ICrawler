package net.ion.icrawler.pipeline;

import java.util.Map;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;

/**
 * Write results in console.<br>
 * Usually used in test.
 */
public class ConsolePipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		System.out.println("get page: " + resultItems.getRequest().getUrl());
		for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
			System.out.println(entry.getKey() + ":\t" + entry.getValue());
		}
	}
}
