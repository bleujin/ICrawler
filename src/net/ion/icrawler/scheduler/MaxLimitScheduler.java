package net.ion.icrawler.scheduler;

import net.ion.framework.util.Debug;
import net.ion.icrawler.Request;
import net.ion.icrawler.Task;

public class MaxLimitScheduler extends DuplicateRemovedScheduler implements Scheduler {

	private Scheduler inner;
	private int max;

	public MaxLimitScheduler(Scheduler inner, int max) {
		this.inner = inner;
		this.max = max;
	}

	@Override
	public void pushWhenNoDuplicate(Request request, Task task) {
		if (max-- > 0){
			inner.push(request, task);
		}
	}

	@Override
	public synchronized Request poll(Task task) {
		Request request = inner.poll(task);
		return request;
	}

}
