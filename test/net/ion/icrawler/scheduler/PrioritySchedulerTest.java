package net.ion.icrawler.scheduler;

import junit.framework.Assert;
import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.Task;
import net.ion.icrawler.scheduler.PriorityScheduler;

import org.junit.Test;

/**
 * <br>
 */
public class PrioritySchedulerTest {

	private PriorityScheduler priorityScheduler = new PriorityScheduler();

	private Task task = new Task() {
		@Override
		public String getUUID() {
			return "1";
		}

		@Override
		public Site getSite() {
			return null;
		}
	};

	@Test
	public void testDifferentPriority() {
		Request request = new Request("a");
		request.setPriority(100);
		priorityScheduler.push(request, task);

		request = new Request("b");
		request.setPriority(900);
		priorityScheduler.push(request, task);

		request = new Request("c");
		priorityScheduler.push(request, task);

		request = new Request("d");
		request.setPriority(-900);
		priorityScheduler.push(request, task);

		Request poll = priorityScheduler.poll(task);
		Assert.assertEquals("b", poll.getUrl());
		poll = priorityScheduler.poll(task);
		Assert.assertEquals("a", poll.getUrl());
		poll = priorityScheduler.poll(task);
		Assert.assertEquals("c", poll.getUrl());
		poll = priorityScheduler.poll(task);
		Assert.assertEquals("d", poll.getUrl());
	}

	@Test
	public void testNoPriority() {
		Request request = new Request("a");
		priorityScheduler.push(request, task);

		request = new Request("b");
		priorityScheduler.push(request, task);

		request = new Request("c");
		priorityScheduler.push(request, task);

		Request poll = priorityScheduler.poll(task);
		Assert.assertEquals("a", poll.getUrl());

		poll = priorityScheduler.poll(task);
		Assert.assertEquals("b", poll.getUrl());

		poll = priorityScheduler.poll(task);
		Assert.assertEquals("c", poll.getUrl());
	}
}
