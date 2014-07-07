package net.ion.icrawler.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.ion.icrawler.Request;
import net.ion.icrawler.Task;

/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 */
//@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

	@Override
	public void pushWhenNoDuplicate(Request request, Task task) {
		queue.add(request);
	}

	@Override
	public synchronized Request poll(Task task) {
		return queue.poll();
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return queue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return getDuplicateRemover().getTotalRequestsCount(task);
	}
}
