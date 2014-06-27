package net.ion.icrawler;

import net.ion.framework.util.ListUtil;
import net.ion.icrawler.selector.Html;
import net.ion.icrawler.selector.Json;
import net.ion.icrawler.selector.Link;
import net.ion.icrawler.selector.Selectable;
import net.ion.icrawler.utils.UrlUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Object storing extracted result and urls to fetch.<br>
 * Not thread safe.<br>
 * Main method： <br>
 * {@link #getUrl()} get url of current page <br>
 * {@link #getHtml()} get content of current page <br>
 * {@link #putField(String, Object)} save extracted result <br>
 * {@link #getResultItems()} get extract results to be used in {@link net.ion.icrawler.pipeline.Pipeline}<br>
 * {@link #addRequests(java.util.List)} {@link #addTarget(String)} add urls to fetch <br>
 * 
 * <br>
 * 
 * @see net.ion.icrawler.downloader.Downloader
 * @see net.ion.icrawler.processor.PageProcessor
 * @since 0.1.0
 */
public class Page {

	private Request request;

	private ResultItems resultItems = new ResultItems();

	private Html html;

	private Json json;

	private String rawText;

	private Selectable url;

	private int statusCode;

	private boolean needCycleRetry;

	private List<Request> targetRequests = ListUtil.newList();

	public Page() {
	}

	public Page setSkip(boolean skip) {
		resultItems.setSkip(skip);
		return this;

	}

	/**
	 * store extract results
	 * 
	 * @param key
	 * @param field
	 */
	public void putField(String key, Object field) {
		resultItems.put(key, field);
	}

	/**
	 * get html content of page
	 * 
	 * @return html
	 */
	public Html getHtml() {
		if (html == null) {
			html = new Html(UrlUtils.fixAllRelativeHrefs(rawText, request.getUrl()));
		}
		return html;
	}

	/**
	 * get json content of page
	 * 
	 * @return json
	 * @since 0.5.0
	 */
	public Json getJson() {
		if (json == null) {
			json = new Json(rawText);
		}
		return json;
	}

	public List<Request> getTargets() {
		return targetRequests;
	}

	/**
	 * add urls to fetch
	 * 
	 * @param requests
	 */
	public void addRequests(List<String> requests) {
		synchronized (targetRequests) {
			for (String s : requests) {
				if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
					continue;
				}
				s = UrlUtils.canonicalizeUrl(s, url.toString());
				targetRequests.add(new Request(s, this.getRequest()));
			}
		}
	}

	public void addRequests(List<String> requests, long priority) {
		synchronized (targetRequests) {
			for (String s : requests) {
				if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
					continue;
				}
				s = UrlUtils.canonicalizeUrl(s, url.toString());
				targetRequests.add(new Request(s, this.request).priority(priority));
			}
		}
	}
	

	public void addTargets(List<Link> requests) {
		synchronized (targetRequests) {
			for (Link link : requests) {
				String s = link.target();
				if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
					continue;
				}
				s = UrlUtils.canonicalizeUrl(s, url.toString());
				targetRequests.add(new Request(s, this.getRequest(), link.anchor()));
			}
		}
	}

	public void addTargets(List<Link> requests, long priority) {
		synchronized (targetRequests) {
			for (Link link : requests) {
				String s = link.target();
				if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
					continue;
				}
				s = UrlUtils.canonicalizeUrl(s, url.toString());
				targetRequests.add(new Request(s, this.getRequest(), link.anchor()).priority(priority));
			}
		}
	}



	public void addTarget(String requestString) {
		if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
			return;
		}
		synchronized (targetRequests) {
			requestString = UrlUtils.canonicalizeUrl(requestString, url.toString());
			targetRequests.add(new Request(requestString, this.request));
		}
	}

	public void addTarget(Request request) {
		synchronized (targetRequests) {
			targetRequests.add(request);
		}
	}

	/**
	 * get url of current page
	 * 
	 * @return url of current page
	 */
	public Selectable getUrl() {
		return url;
	}

	public void setUrl(Selectable url) {
		this.url = url;
	}

	@Deprecated
	public void setHtml(Html html){
		this.html = html ;
	}
	
	/**
	 * get request of current page
	 * 
	 * @return request
	 */
	public Request getRequest() {
		return request;
	}

	public boolean isNeedCycleRetry() {
		return needCycleRetry;
	}

	public void setNeedCycleRetry(boolean needCycleRetry) {
		this.needCycleRetry = needCycleRetry;
	}

	public void setRequest(Request request) {
		this.request = request;
		this.resultItems.setRequest(request);
	}

	public ResultItems getResultItems() {
		return resultItems;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getRawText() {
		return rawText;
	}

	public Page setRawText(String rawText) {
		this.rawText = rawText;
		return this;
	}

	@Override
	public String toString() {
		return "Page{" + "request=" + request + ", resultItems=" + resultItems + ", rawText='" + rawText + '\'' + ", url=" + url + ", statusCode=" + statusCode + ", targetRequests=" + targetRequests + '}';
	}

}
