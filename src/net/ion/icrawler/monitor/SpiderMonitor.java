package net.ion.icrawler.monitor;

import net.ion.icrawler.Request;
import net.ion.icrawler.Spider;
import net.ion.icrawler.SpiderListener;
import net.ion.icrawler.utils.Experimental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Experimental
public class SpiderMonitor {

	private static SpiderMonitor INSTANCE = new SpiderMonitor();

	private AtomicBoolean started = new AtomicBoolean(false);

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MBeanServer mbeanServer;

	private String jmxServerName;

	private List<SpiderStatusMXBean> spiderStatuses = new ArrayList<SpiderStatusMXBean>();

	protected SpiderMonitor() {
		jmxServerName = "WebMagic";
		mbeanServer = ManagementFactory.getPlatformMBeanServer();
	}

	/**
	 * Register spider for monitor.
	 * 
	 * @param spiders
	 * @return
	 */
	public synchronized SpiderMonitor register(Spider... spiders) throws JMException {
		for (Spider spider : spiders) {
			MonitorSpiderListener monitorSpiderListener = new MonitorSpiderListener();
			if (spider.getSpiderListeners() == null) {
				List<SpiderListener> spiderListeners = new ArrayList<SpiderListener>();
				spiderListeners.add(monitorSpiderListener);
				spider.setSpiderListeners(spiderListeners);
			} else {
				spider.getSpiderListeners().add(monitorSpiderListener);
			}
			SpiderStatusMXBean spiderStatusMBean = getSpiderStatusMBean(spider, monitorSpiderListener);
			registerMBean(spiderStatusMBean);
			spiderStatuses.add(spiderStatusMBean);
		}
		return this;
	}

	protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener) {
		return new SpiderStatus(spider, monitorSpiderListener);
	}

	public static SpiderMonitor instance() {
		return INSTANCE;
	}

	public class MonitorSpiderListener implements SpiderListener {

		private final AtomicInteger successCount = new AtomicInteger(0);

		private final AtomicInteger errorCount = new AtomicInteger(0);

		private List<String> errorUrls = Collections.synchronizedList(new ArrayList<String>());

		@Override
		public void onSuccess(Request request) {
			successCount.incrementAndGet();
		}

		@Override
		public void onError(Request request) {
			errorUrls.add(request.getUrl());
			errorCount.incrementAndGet();
		}

		public AtomicInteger getSuccessCount() {
			return successCount;
		}

		public AtomicInteger getErrorCount() {
			return errorCount;
		}

		public List<String> getErrorUrls() {
			return errorUrls;
		}
	}

	protected void registerMBean(SpiderStatusMXBean spiderStatus) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		ObjectName objName = new ObjectName(jmxServerName + ":name=" + spiderStatus.getName());
		mbeanServer.registerMBean(spiderStatus, objName);
	}

}
