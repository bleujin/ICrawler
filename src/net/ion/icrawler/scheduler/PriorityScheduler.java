package net.ion.icrawler.scheduler;

import net.ion.icrawler.Request;
import net.ion.icrawler.Task;
import net.ion.icrawler.utils.NumberUtils;

import org.apache.http.annotation.ThreadSafe;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Priority scheduler. Request with higher priority will poll earlier. <br>
 */
@ThreadSafe
public class PriorityScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	public static final int INITIAL_CAPACITY = 5;

	private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();

	private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY, new Comparator<Request>() {
		@Override
		public int compare(Request o1, Request o2) {
			return -NumberUtils.compareLong(o1.priority(), o2.priority());
		}
	});

	private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY, new Comparator<Request>() {
		@Override
		public int compare(Request o1, Request o2) {
			return -NumberUtils.compareLong(o1.priority(), o2.priority());
		}
	});

	@Override
	public void pushWhenNoDuplicate(Request request, Task task) {
		if (request.priority() == 0) {
			noPriorityQueue.add(request);
		} else if (request.priority() > 0) {
			priorityQueuePlus.put(request);
		} else {
			priorityQueueMinus.put(request);
		}
	}

	@Override
	public synchronized Request poll(Task task) {
		Request poll = priorityQueuePlus.poll();
		if (poll != null) {
			return poll;
		}
		poll = noPriorityQueue.poll();
		if (poll != null) {
			return poll;
		}
		return priorityQueueMinus.poll();
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return noPriorityQueue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return getDuplicateRemover().getTotalRequestsCount(task);
	}
}
