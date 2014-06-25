package net.ion.icrawler.pipeline;

import net.ion.framework.util.Debug;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;

public class DebugPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		Debug.line(resultItems.getRequest());
	}

}
