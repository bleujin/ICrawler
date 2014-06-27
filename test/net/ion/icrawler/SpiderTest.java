package net.ion.icrawler;

import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Site;
import net.ion.icrawler.Spider;
import net.ion.icrawler.Task;
import net.ion.icrawler.downloader.Downloader;
import net.ion.icrawler.pipeline.Pipeline;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.Scheduler;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**

 */
public class SpiderTest {

	@Ignore("long time")
	@Test
	public void testStartAndStop() throws InterruptedException {
		Spider spider = Site.create("http://www.oschina.net/").createSpider(new SimplePageProcessor("http://www.oschina.net/*")).addPipeline(new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				System.out.println(1);
			}
		}).thread(1);
		spider.start();
		Thread.sleep(10000);
		spider.stop();
		Thread.sleep(10000);
		spider.start();
		Thread.sleep(10000);
	}

	@Ignore("long time")
	@Test
	public void testWaitAndNotify() throws InterruptedException {
		for (int i = 0; i < 10000; i++) {
			System.out.println("round " + i);
			testRound();
		}
	}

	private void testRound() {
		Spider spider = Site.me().sleepTime(0).createSpider(new PageProcessor() {

			private AtomicInteger count = new AtomicInteger();

			@Override
			public void process(Page page) {
				page.setSkip(true);
			}
		}).setDownloader(new Downloader() {
			@Override
			public Page download(Request request, Task task) {
				return new Page().setRawText("");
			}

			@Override
			public void setThread(int threadNum) {

			}
		}).setScheduler(new Scheduler() {

			private AtomicInteger count = new AtomicInteger();

			private Random random = new Random();

			@Override
			public void push(Request request, Task task) {

			}

			@Override
			public synchronized Request poll(Task task) {
				if (count.incrementAndGet() > 1000) {
					return null;
				}
				if (random.nextInt(100) > 90) {
					return null;
				}
				return new Request("test");
			}
		}).thread(10);
		spider.run();
	}
}
