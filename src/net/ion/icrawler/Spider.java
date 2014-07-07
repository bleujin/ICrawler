package net.ion.icrawler;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.icrawler.downloader.AClientDownloader;
import net.ion.icrawler.downloader.Downloader;
import net.ion.icrawler.pipeline.CollectorPipeline;
import net.ion.icrawler.pipeline.ConsolePipeline;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.pipeline.ResultItemsCollectorPipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.proxy.HttpHost;
import net.ion.icrawler.scheduler.QueueScheduler;
import net.ion.icrawler.scheduler.Scheduler;
import net.ion.icrawler.selector.thread.CountableThreadPool;
import net.ion.icrawler.utils.UrlUtils;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Entrance of a crawler.<br>
 * A spider contains four modules: Downloader, Scheduler, PageProcessor and Pipeline.<br>
 * Every module is a field of Spider. <br>
 * The modules are defined in interface. <br>
 * You can customize a spider with various implementations of them. <br>
 * @see Downloader
 * @see Scheduler
 * @see PageProcessor
 * @see Pipeline
 */
public class Spider implements Runnable, Task {

	protected Downloader downloader;

	protected List<Pipeline> pipelines = new ArrayList<Pipeline>();

	protected PageProcessor pageProcessor;

	protected List<Request> startRequests;

	protected Site site;

	protected String uuid;

	protected Scheduler scheduler = new QueueScheduler();

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected CountableThreadPool threadPool;

	protected ExecutorService executorService;

	protected int threadNum = 1;

	protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

	protected boolean exitWhenComplete = true;

	protected final static int STAT_INIT = 0;

	protected final static int STAT_RUNNING = 1;

	protected final static int STAT_STOPPED = 2;

	protected boolean spawnUrl = true;

	protected boolean destroyWhenExit = true;

	private ReentrantLock newUrlLock = new ReentrantLock();

	private Condition newUrlCondition = newUrlLock.newCondition();

	private List<SpiderListener> spiderListeners;

	private final AtomicLong pageCount = new AtomicLong(0);

	private Date startTime;

	private int emptySleepTime = 30000;


	public static Spider create(Site site, PageProcessor pageProcessor) {
		return new Spider(site, pageProcessor);
	}

	public Spider(Site site, PageProcessor pageProcessor) {
		this.site = site ;
		this.pageProcessor = pageProcessor;
		this.startRequests = ListUtil.newList();
	}


	
	public Spider startUrls(String... startUrls) {
		checkIfRunning();
		this.startRequests = UrlUtils.convertToRequests(ListUtil.toList(startUrls));
		return this;
	}

	public Spider startRequest(List<Request> startRequests) {
		checkIfRunning();
		this.startRequests = startRequests;
		return this;
	}

	public Spider setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}
	

	public Spider scheduler(Scheduler scheduler) {
		return setScheduler(scheduler);
	}

	public Spider setScheduler(Scheduler scheduler) {
		checkIfRunning();
		Scheduler oldScheduler = this.scheduler;
		this.scheduler = scheduler;
		if (oldScheduler != null) {
			Request request;
			while ((request = oldScheduler.poll(this)) != null) {
				this.scheduler.push(request, this);
			}
		}
		return this;
	}

	public Spider addPipeline(Pipeline pipeline) {
		checkIfRunning();
		this.pipelines.add(pipeline);
		return this;
	}

	public Spider setPipelines(List<Pipeline> pipelines) {
		checkIfRunning();
		this.pipelines = pipelines;
		return this;
	}

	public Spider clearPipeline() {
		pipelines = new ArrayList<Pipeline>();
		return this;
	}

	public Spider setDownloader(Downloader downloader) {
		checkIfRunning();
		this.downloader = downloader;
		return this;
	}

	protected void initComponent() {
		if (downloader == null) {
			this.downloader = new AClientDownloader();
		}
		if (pipelines.isEmpty()) {
			pipelines.add(new ConsolePipeline());
		}
		downloader.setThread(threadNum);
		if (threadPool == null || threadPool.isShutdown()) {
			if (executorService != null && !executorService.isShutdown()) {
				threadPool = new CountableThreadPool(threadNum, executorService);
			} else {
				threadPool = new CountableThreadPool(threadNum);
			}
		}
		if (startRequests != null) {
			for (Request request : startRequests) {
				scheduler.push(request, this);
			}
			startRequests.clear();
		}
		startTime = new Date();
	}

	@Override
	public void run() {
		checkRunningStat();
		initComponent();
		logger.info("Spider " + getUUID() + " started!");
		
		Request loginRequest = site.loginRequest();
		if (loginRequest != null){
			boolean success = downloader.login(loginRequest, this);
			if (! success) {
				throw new IllegalStateException("not logined[request" + loginRequest + "]") ;
			}
		}
		
		
		while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
			Request request = scheduler.poll(this);
			if (request == null) {
				if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
					break;
				}
				// wait until new url added
				waitNewUrl();
			} else {
				final Request requestFinal = request;
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							processRequest(requestFinal);
							onSuccess(requestFinal);
						} catch (Exception e) {
							onError(requestFinal);
							e.printStackTrace();
							logger.error("process request " + requestFinal + " error", e);
						} finally {
							if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
								site.returnHttpProxyToPool((HttpHost) requestFinal.asObject(Request.PROXY), (Integer) requestFinal.asObject(Request.STATUS_CODE));
							}
							pageCount.incrementAndGet();
							signalNewUrl();
						}
					}
				});
			}
		}
		stat.set(STAT_STOPPED);
		// release some resources
		if (destroyWhenExit) {
			close();
		}
	}

	protected void onError(Request request) {
		if (CollectionUtils.isNotEmpty(spiderListeners)) {
			for (SpiderListener spiderListener : spiderListeners) {
				spiderListener.onError(request);
			}
		}
	}

	protected void onSuccess(Request request) {
		if (CollectionUtils.isNotEmpty(spiderListeners)) {
			for (SpiderListener spiderListener : spiderListeners) {
				spiderListener.onSuccess(request);
			}
		}
	}

	private void checkRunningStat() {
		while (true) {
			int statNow = stat.get();
			if (statNow == STAT_RUNNING) {
				throw new IllegalStateException("Spider is already running!");
			}
			if (stat.compareAndSet(statNow, STAT_RUNNING)) {
				break;
			}
		}
	}

	public void close() {
		destroyEach(downloader);
		destroyEach(pageProcessor);
		for (Pipeline pipeline : pipelines) {
			destroyEach(pipeline);
		}
		threadPool.shutdown();
	}

	private void destroyEach(Object object) {
		if (object instanceof Closeable) {
			try {
				((Closeable) object).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void test(String... urls) {
		initComponent();
		if (urls.length > 0) {
			for (String url : urls) {
				processRequest(new Request(url));
			}
		}
	}

	protected void processRequest(Request request) {
		Page page = downloader.download(request, this);
		
		if (page == null) {
			sleep(site.sleepTime());
			onError(request);
			return;
		}
		// for cycle retry
		if (page.isNeedCycleRetry()) {
			extractAndAddRequests(page, true);
			sleep(site.sleepTime());
			return;
		}
		pageProcessor.process(page);
		extractAndAddRequests(page, spawnUrl);
		if (!page.getResultItems().isSkip()) {
			for (Pipeline pipeline : pipelines) {
				pipeline.process(page.getResultItems(), this);
			}
		}
		// for proxy status management
		request.putExtra(Request.STATUS_CODE, page.getStatusCode());
		sleep(site.sleepTime());
	}

	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void extractAndAddRequests(Page page, boolean spawnUrl) {
		if (spawnUrl && CollectionUtils.isNotEmpty(page.getTargets())) {
			for (Request request : page.getTargets()) {
				addRequest(request);
			}
		}
	}

	private void addRequest(Request request) {
		if (site.getDomain() == null && request != null && request.getUrl() != null) {
			site.setDomain(UrlUtils.getDomain(request.getUrl()));
		}
		scheduler.push(request, this);
	}

	protected void checkIfRunning() {
		if (stat.get() == STAT_RUNNING) {
			throw new IllegalStateException("Spider is already running!");
		}
	}

	public void runAsync() {
		Thread thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
	}


	public Spider addUrl(String... urls) {
		for (String url : urls) {
			addRequest(new Request(url));
		}
		signalNewUrl();
		return this;
	}

	/**
	 * Download urls synchronizing.
	 * 
	 * @param urls
	 * @return
	 */
	public <T> List<T> getAll(Collection<String> urls) {
		destroyWhenExit = false;
		spawnUrl = false;
		startRequests.clear();
		for (Request request : UrlUtils.convertToRequests(urls)) {
			addRequest(request);
		}
		CollectorPipeline collectorPipeline = getCollectorPipeline();
		pipelines.add(collectorPipeline);
		run();
		spawnUrl = true;
		destroyWhenExit = true;
		return collectorPipeline.getCollected();
	}

	protected CollectorPipeline getCollectorPipeline() {
		return new ResultItemsCollectorPipeline();
	}

	public <T> T get(String url) {
		List<String> urls = Lists.newArrayList(url);
		List<T> resultItemses = getAll(urls);
		if (resultItemses != null && resultItemses.size() > 0) {
			return resultItemses.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Add urls with information to crawl.<br/>
	 * 
	 * @param requests
	 * @return
	 */
	public Spider addRequest(Request... requests) {
		for (Request request : requests) {
			addRequest(request);
		}
		signalNewUrl();
		return this;
	}

	private void waitNewUrl() {
		newUrlLock.lock();
		try {
			// double check
			if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
				return;
			}
			newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn("waitNewUrl - interrupted, error {}", e);
		} finally {
			newUrlLock.unlock();
		}
	}

	private void signalNewUrl() {
		try {
			newUrlLock.lock();
			newUrlCondition.signalAll();
		} finally {
			newUrlLock.unlock();
		}
	}

	public void start() {
		runAsync();
	}

	public void stop() {
		if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
			logger.info("Spider " + getUUID() + " stop success!");
		} else {
			logger.info("Spider " + getUUID() + " stop fail!");
		}
	}

	/**
	 * start with more than one threads
	 * 
	 * @param threadNum
	 * @return this
	 */
	public Spider thread(int threadNum) {
		checkIfRunning();
		this.threadNum = threadNum;
		if (threadNum <= 0) {
			throw new IllegalArgumentException("threadNum should be more than one!");
		}
		return this;
	}

	/**
	 * start with more than one threads
	 * 
	 * @param threadNum
	 * @return this
	 */
	public Spider thread(ExecutorService executorService, int threadNum) {
		checkIfRunning();
		this.threadNum = threadNum;
		if (threadNum <= 0) {
			throw new IllegalArgumentException("threadNum should be more than one!");
		}
		return this;
	}

	public boolean isExitWhenComplete() {
		return exitWhenComplete;
	}

	/**
	 * Exit when complete. <br/>
	 * True: exit when all url of the site is downloaded. <br/>
	 * False: not exit until call stop() manually.<br/>
	 * 
	 * @param exitWhenComplete
	 * @return
	 */
	public Spider setExitWhenComplete(boolean exitWhenComplete) {
		this.exitWhenComplete = exitWhenComplete;
		return this;
	}

	public boolean isSpawnUrl() {
		return spawnUrl;
	}

	/**
	 * Get page count downloaded by spider.
	 * 
	 * @return total downloaded page count
	 * @since 0.4.1
	 */
	public long getPageCount() {
		return pageCount.get();
	}

	/**
	 * Get running status by spider.
	 * 
	 * @return running status
	 * @see Status
	 * @since 0.4.1
	 */
	public Status getStatus() {
		return Status.fromValue(stat.get());
	}

	public enum Status {
		Init(0), Running(1), Stopped(2);

		private Status(int value) {
			this.value = value;
		}

		private int value;

		int getValue() {
			return value;
		}

		public static Status fromValue(int value) {
			for (Status status : Status.values()) {
				if (status.getValue() == value) {
					return status;
				}
			}
			// default value
			return Init;
		}
	}

	/**
	 * Get thread count which is running
	 * 
	 * @return thread count which is running
	 * @since 0.4.1
	 */
	public int getThreadAlive() {
		if (threadPool == null) {
			return 0;
		}
		return threadPool.getThreadAlive();
	}

	/**
	 * Whether add urls extracted to download.<br>
	 * Add urls to download when it is true, and just download seed urls when it is false. <br>
	 * DO NOT set it unless you know what it means!
	 * 
	 * @param spawnUrl
	 * @return
	 * @since 0.4.0
	 */
	public Spider setSpawnUrl(boolean spawnUrl) {
		this.spawnUrl = spawnUrl;
		return this;
	}

	@Override
	public String getUUID() {
		if (uuid != null) {
			return uuid;
		}
		if (site != null) {
			return site.getDomain();
		}
		uuid = UUID.randomUUID().toString();
		return uuid;
	}

	public Spider setExecutorService(ExecutorService executorService) {
		checkIfRunning();
		this.executorService = executorService;
		return this;
	}

	@Override
	public Site getSite() {
		return site;
	}

	public List<SpiderListener> getSpiderListeners() {
		return spiderListeners;
	}

	public Spider setSpiderListeners(List<SpiderListener> spiderListeners) {
		this.spiderListeners = spiderListeners;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Set wait time when no url is polled.<br>
	 * </br>
	 * 
	 * @param emptySleepTime In MILLISECONDS.
	 */
	public void setEmptySleepTime(int emptySleepTime) {
		this.emptySleepTime = emptySleepTime;
	}
}
