package net.ion.icrawler.monitor;

import net.ion.icrawler.Spider;
import net.ion.icrawler.monitor.SpiderMonitor;
import net.ion.icrawler.monitor.SpiderStatus;

/**
 * @author code4crafer@gmail.com
 */
public class CustomSpiderStatus extends SpiderStatus implements CustomSpiderStatusMXBean {

	public CustomSpiderStatus(Spider spider, SpiderMonitor.MonitorSpiderListener monitorSpiderListener) {
		super(spider, monitorSpiderListener);
	}

	@Override
	public String getSchedulerName() {
		return spider.getScheduler().getClass().getName();
	}
}
