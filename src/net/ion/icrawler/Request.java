package net.ion.icrawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.restlet.data.Method;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.icrawler.utils.Experimental;
import net.ion.radon.aclient.FluentStringsMap;
import net.ion.radon.aclient.simple.HeaderConstant;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 2062192774891352043L;

	public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
	public static final String STATUS_CODE = "StatusCode";
	public static final String PROXY = "proxy";

	private String url;

	private Method method = Method.GET;

	/**
	 * Store additional information in extras.
	 */
	private Map<String, Object> extras;

	/**
	 * Priority of the request.<br>
	 * The bigger will be processed earlier. <br>
	 * 
	 * @see net.ion.icrawler.scheduler.PriorityScheduler
	 */
	private long priority;

	private List<NameValuePair> params;

	public Request(String url) {
		this.url = url;
	}

	public Request(String url, Method method) {
		this.url = url;
		this.method = method ;
	}

	public Request(String url, Request referer) {
		this.url = url;
		putExtra(HeaderConstant.HEADER_REFERRER, referer.getUrl()) ;
	}

	public Request(String url, Request referer, String anchor) {
		this.url = url;
		putExtra(HeaderConstant.HEADER_REFERRER, referer.getUrl()) ;
		putExtra("Anchor", anchor) ;
	}


	
	public long priority() {
		return priority;
	}

	/**
	 * Set the priority of request for sorting.<br>
	 * Need a scheduler supporting priority.<br>
	 * 
	 * @see net.ion.icrawler.scheduler.PriorityScheduler
	 * 
	 * @param priority
	 * @return this
	 */
	
	@Experimental
	public Request priority(long priority) {
		this.priority = priority;
		return this;
	}

	public <T> T asObject(String key) {
		if (extras == null) {
			return null;
		}
		return (T)extras.get(key);
	}

	
	public String asString(String key) {
		return ObjectUtil.toString(asObject(key));
	}

	public int asInt(String key) {
		return asObject(key);
	}
	
	public Request putExtra(String key, Object value) {
		if (extras == null) {
			extras = new HashMap<String, Object>();
		}
		extras.put(key, value);
		return this;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Request request = (Request) o;

		if (!url.equals(request.url))
			return false;

		return true;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}
	
	public Request addParameter(String name, String... values){
		if (params == null){
			params = ListUtil.newList() ;
			this.putExtra("_parameters", params) ;
		}
		
		for (String value : values) {
			params.add(new BasicNameValuePair(name, value)) ;
		}
		
		return this ;
	}

	public Request setUrl(String url) {
		this.url = url;
		return this ;
	}


	public Method getMethod() {
		return method;
	}

	public Request setMethod(String method) {
		this.method = Method.valueOf(method);
		return this ;
	}

	@Override
	public String toString() {
		return "Request{" + "url='" + url + '\'' + ", method='" + method + '\'' + ", extras=" + extras + ", priority=" + priority + '}';
	}

	public NameValuePair[] getParameters() {
		return params == null ? new NameValuePair[0] : params.toArray(new NameValuePair[0]);
	}

}
