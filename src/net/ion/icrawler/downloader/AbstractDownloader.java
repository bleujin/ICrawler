package net.ion.icrawler.downloader;

import net.ion.framework.util.Debug;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.selector.Html;

/**
 * Base class of downloader with some common methods.
 */
public abstract class AbstractDownloader implements Downloader {


//	public Html download(String url) {
//		return download(url, null);
//	}
//	public Html download(String url, String charset) {
//		Page page = download(new Request(url), Site.create().setCharset(charset).toTask());
//		return (Html) page.getHtml();
//	}

	protected void onSuccess(Request request) {
	}

	protected void onError(Request request) {

	}

	protected Page addToCycleRetry(Request request, Site site) {
		Page page = new Page();
		Object cycleTriedTimesObject = request.asObject(Request.CYCLE_TRIED_TIMES);
		if (cycleTriedTimesObject == null) {
			page.addTarget(request.priority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
		} else {
			int cycleTriedTimes = (Integer) cycleTriedTimesObject;
			cycleTriedTimes++;
			if (cycleTriedTimes >= site.getCycleRetryTimes()) {
				return null;
			}
			page.addTarget(request.priority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
		}
		page.setNeedCycleRetry(true);
		return page;
	}
}
