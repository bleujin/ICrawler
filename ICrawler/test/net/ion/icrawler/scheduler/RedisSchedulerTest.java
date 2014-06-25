package net.ion.icrawler.scheduler;

import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.Task;
import net.ion.icrawler.scheduler.RedisScheduler;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <br>
 */
public class RedisSchedulerTest {

	private RedisScheduler redisScheduler;

	@Before
	public void setUp() {
		redisScheduler = new RedisScheduler("localhost");
	}

	@Ignore("environment depended")
	@Test
	public void test() {
		Task task = new Task() {
			@Override
			public String getUUID() {
				return "1";
			}

			@Override
			public Site getSite() {
				return null;
			}
		};
		Request request = new Request("http://www.ibm.com/developerworks/cn/java/j-javadev2-22/");
		request.putExtra("1", "2");
		redisScheduler.push(request, task);
		Request poll = redisScheduler.poll(task);
		System.out.println(poll);

	}
}
