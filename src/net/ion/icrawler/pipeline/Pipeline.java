package net.ion.icrawler.pipeline;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 * 
 * <br>
 * 
 * @since 0.1.0
 * @see ConsolePipeline
 * @see FilePipeline
 */
public interface Pipeline {

	/**
	 * Process extracted results.
	 * 
	 * @param resultItems
	 * @param task
	 */
	public void process(ResultItems ritems, Task task);
}
