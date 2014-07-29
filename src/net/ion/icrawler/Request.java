package net.ion.icrawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.icrawler.utils.Experimental;
import net.ion.icrawler.utils.UrlUtils;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.StringPart;
import net.ion.radon.aclient.simple.HeaderConstant;

import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * Object contains url to crawl.
 * It contains some additional information.
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 2062192774891352043L;

	public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
	public static final String STATUS_CODE = "StatusCode";
	public static final String PROXY = "proxy";

	private String url;

	private HttpMethod method = HttpMethod.GET;

	/**
	 * Store additional information in extras.
	 */
	private Map<String, Object> extras;

	/**
	 * Priority of the request. 
	 * The bigger will be processed earlier.  
	 * 
	 * @see net.ion.icrawler.scheduler.PriorityScheduler
	 */
	private long priority;

	private List<StringPart> params;

	private Realm realm;

	public Request(String url) {
		this.url = url;
	}

	public Request(String url, HttpMethod method) {
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
	 * Set the priority of request for sorting. 
	 * Need a scheduler supporting priority. 
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
			params.add(new StringPart(name, value)) ;
		}
		
		return this ;
	}

	public Request setUrl(String url) {
		this.url = url;
		return this ;
	}


	public HttpMethod getMethod() {
		return method;
	}

	public Request setMethod(String method) {
		this.method = HttpMethod.valueOf(method);
		return this ;
	}

	@Override
	public String toString() {
		return "Request{" + "url='" + url + '\'' + ", method='" + method + '\'' + ", extras=" + extras + ", priority=" + priority + '}';
	}

	public StringPart[] getParameters() {
		return params == null ? new StringPart[0] : params.toArray(new StringPart[0]);
	}
	
	public Map<String, String> getQueryParameter(){
		return UrlUtils.parseQueryString(StringUtil.substringAfter(url, "?")) ;
	}

	public Request realm(Realm realm) {
		this.realm = realm ;
		return this;
	}

	public Realm realm() {
		return realm;
	}

}
