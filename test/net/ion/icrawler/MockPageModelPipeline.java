package net.ion.icrawler;

import net.ion.icrawler.Task;
import net.ion.icrawler.pipeline.PageModelPipeline;
import junit.framework.Assert;

/**

 */
public class MockPageModelPipeline implements PageModelPipeline {
	@Override
	public void process(Object o, Task task) {
		Assert.assertNotNull(o);
	}
}
