package net.ion.icrawler;

/**
 * Interface for identifying different tasks.<br>
 * @see net.ion.icrawler.scheduler.Scheduler
 * @see net.ion.icrawler.pipeline.Pipeline
 */
public interface Task {

	/**
	 * unique id for a task.
	 * 
	 * @return uuid
	 */
	public String getUUID();

	/**
	 * site of a task
	 * 
	 * @return site
	 */
	public Site getSite();

}
