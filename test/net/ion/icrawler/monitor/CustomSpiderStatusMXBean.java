package net.ion.icrawler.monitor;

import net.ion.icrawler.monitor.SpiderStatusMXBean;

/**
 * @author code4crafer@gmail.com
 */
public interface CustomSpiderStatusMXBean extends SpiderStatusMXBean {

	public String getSchedulerName();

}
