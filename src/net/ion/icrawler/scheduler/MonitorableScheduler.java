package net.ion.icrawler.scheduler;

import net.ion.icrawler.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 * 
 * 
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {

	public int getLeftRequestsCount(Task task);

	public int getTotalRequestsCount(Task task);

}