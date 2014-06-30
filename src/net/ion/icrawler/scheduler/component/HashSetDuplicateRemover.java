package net.ion.icrawler.scheduler.component;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.icrawler.Request;
import net.ion.icrawler.Task;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

	private Set<String> urls = Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	@Override
	public boolean isDuplicate(Request request, Task task) {
		String url = getUrl(request);
		if (url == null) return true ;
		return !urls.add(url);
	}

	protected String getUrl(Request request) {
		return request.getUrl();
	}

	@Override
	public void resetDuplicateCheck(Task task) {
		urls.clear();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return urls.size();
	}
}
