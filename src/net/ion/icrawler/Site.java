package net.ion.icrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.pipeline.PageModelPipeline;
import net.ion.icrawler.processor.BinaryHandler;
import net.ion.icrawler.processor.PageProcessor;
import net.ion.icrawler.proxy.ProxyPool;

import org.apache.http.HttpHost;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * Object contains setting for crawler.<br>
 */
public class Site {

	private String domain;

	private String userAgent = "icrawler/2.0 (i-on.net)";

	private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();

	private Table<String, String, String> cookies = HashBasedTable.create();

	private String charset;

	/**
	 * startUrls is the urls the crawler to start with.
	 */
	private List<Request> startRequests = new ArrayList<Request>();

	private int sleepTime = 1000;

	private int retryTimes = 0;

	private int cycleRetryTimes = 0;

	private int timeOut = 5000;

	private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

	private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

	private Map<String, String> headers = new HashMap<String, String>();

	private HttpHost httpProxy;

	private ProxyPool httpProxyPool;

	private boolean useGzip = true;

	private Request loginRequest;

	private BinaryHandler bhandler = BinaryHandler.BLANK;

	static {
		DEFAULT_STATUS_CODE_SET.add(200);
	}

	/**
	 * new a Site
	 */
	public static Site create() {
		return new Site();
	}

	public static Site create(String domain) {
		return new Site().setDomain(domain);
	}

	public static Site test(){
		return create("http://bleujin.tistory.com/") ;
	}


	public Spider newSpider(PageProcessor processor) {
		return Spider.create(this, processor).startUrls(domain);
	}

	public <T> OOSpider<T> createOOSpider(PageModelPipeline processor, Class<T> clz) {
		return OOSpider.create(this, processor, clz) ;
	}

	public <T> OOSpider<T> createOOSpider(Class... pageModel) {
		return OOSpider.create(this, pageModel);
	}

	

	public Site loginRequest(Request login) {
		this.loginRequest = login ;
		return this;
	}
	
	public Request loginRequest(){
		return loginRequest ;
	}

	public Site addCookie(String name, String value) {
		defaultCookies.put(name, value);
		return this;
	}

	public Site addCookie(String domain, String name, String value) {
		cookies.put(domain, name, value);
		return this;
	}

	public Map<String, String> getCookies() {
		return defaultCookies;
	}

	public Map<String, Map<String, String>> getAllCookies() {
		return cookies.rowMap();
	}

	public String getUserAgent() {
		return userAgent;
	}

	public Site setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	
	public String getDomain() {
		return domain;
	}

	public Site setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public Site setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public Site setTimeOut(int timeOut) {
		this.timeOut = timeOut;
		return this;
	}

	public Site setAcceptStatCode(Set<Integer> acceptStatCode) {
		this.acceptStatCode = acceptStatCode;
		return this;
	}

	public Set<Integer> getAcceptStatCode() {
		return acceptStatCode;
	}

	public Site sleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}

	public int sleepTime() {
		return sleepTime;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Site addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public Site setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
		return this;
	}

	public int getCycleRetryTimes() {
		return cycleRetryTimes;
	}

	public Site setCycleRetryTimes(int cycleRetryTimes) {
		this.cycleRetryTimes = cycleRetryTimes;
		return this;
	}

	public HttpHost getHttpProxy() {
		return httpProxy;
	}

	public Site setHttpProxy(HttpHost httpProxy) {
		this.httpProxy = httpProxy;
		return this;
	}

	public boolean isUseGzip() {
		return useGzip;
	}

	public Site setUseGzip(boolean useGzip) {
		this.useGzip = useGzip;
		return this;
	}

	public Task toTask() {
		return new Task() {
			@Override
			public String getUUID() {
				return Site.this.getDomain();
			}

			@Override
			public Site getSite() {
				return Site.this;
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Site site = (Site) o;

		if (cycleRetryTimes != site.cycleRetryTimes)
			return false;
		if (retryTimes != site.retryTimes)
			return false;
		if (sleepTime != site.sleepTime)
			return false;
		if (timeOut != site.timeOut)
			return false;
		if (acceptStatCode != null ? !acceptStatCode.equals(site.acceptStatCode) : site.acceptStatCode != null)
			return false;
		if (charset != null ? !charset.equals(site.charset) : site.charset != null)
			return false;
		if (defaultCookies != null ? !defaultCookies.equals(site.defaultCookies) : site.defaultCookies != null)
			return false;
		if (domain != null ? !domain.equals(site.domain) : site.domain != null)
			return false;
		if (headers != null ? !headers.equals(site.headers) : site.headers != null)
			return false;
		if (userAgent != null ? !userAgent.equals(site.userAgent) : site.userAgent != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = domain != null ? domain.hashCode() : 0;
		result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
		result = 31 * result + (defaultCookies != null ? defaultCookies.hashCode() : 0);
		result = 31 * result + (charset != null ? charset.hashCode() : 0);
		result = 31 * result + sleepTime;
		result = 31 * result + retryTimes;
		result = 31 * result + cycleRetryTimes;
		result = 31 * result + timeOut;
		result = 31 * result + (acceptStatCode != null ? acceptStatCode.hashCode() : 0);
		result = 31 * result + (headers != null ? headers.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Site{" + "domain='" + domain + '\'' + ", userAgent='" + userAgent + '\'' + ", cookies=" + defaultCookies + ", charset='" + charset + '\'' + ", sleepTime=" + sleepTime + ", retryTimes=" + retryTimes + ", cycleRetryTimes=" + cycleRetryTimes
				+ ", timeOut=" + timeOut + ", acceptStatCode=" + acceptStatCode + ", headers=" + headers + '}';
	}

	/**
	 * Set httpProxyPool, String[0]:ip, String[1]:port <br>
	 * 
	 * @return this
	 */
	public Site setHttpProxyPool(List<String[]> httpProxyList) {
		this.httpProxyPool = new ProxyPool(httpProxyList, false);
		return this;
	}

	public Site enableHttpProxyPool() {
		this.httpProxyPool = new ProxyPool();
		return this;
	}

	public ProxyPool getHttpProxyPool() {
		return httpProxyPool;
	}

	public HttpHost getHttpProxyFromPool() {
		return httpProxyPool.getProxy();
	}

	public void returnHttpProxyToPool(HttpHost proxy, int statusCode) {
		httpProxyPool.returnProxy(proxy, statusCode);
	}

	public Site setProxyReuseInterval(int reuseInterval) {
		this.httpProxyPool.setReuseInterval(reuseInterval);
		return this;
	}

	public BinaryHandler getBinaryHandler() {
		return bhandler;
	}

	public Site binaryHandler(BinaryHandler binaryHandler) {
		this.bhandler = binaryHandler ;
		return this;
	}







}
