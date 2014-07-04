package net.ion.icrawler.downloader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.Task;
import net.ion.icrawler.processor.BinaryHandler;
import net.ion.icrawler.selector.PlainText;
import net.ion.icrawler.utils.UrlUtils;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.ProxyServer;
import net.ion.radon.aclient.ProxyServer.Protocol;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.simple.HeaderConstant;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The http downloader based on HttpClient.
 */
@ThreadSafe
public class AClientDownloader extends AbstractDownloader {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, NewClient> httpClients = new HashMap<String, NewClient>();
	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
	
	private Map<String, List<Cookie>> cookiesPerDomain = MapUtil.newMap() ;

	private NewClient getHttpClient(Site site) {
		String domain = site.getDomain();
		NewClient httpClient = httpClients.get(domain);
//		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut()).setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.BEST_MATCH);
		if (httpClient == null) {
			synchronized (this) {
				httpClient = httpClients.get(domain);
				if (httpClient == null) {
					ClientConfig cconfig = ClientConfig.newBuilder().setRequestTimeoutInMs(site.getTimeOut()).setConnectionTimeoutInMs(site.getTimeOut()).setMaxRequestRetry(site.getRetryTimes()).setUserAgent(site.getUserAgent()).build() ;
					httpClient = NewClient.create(cconfig) ;
					httpClients.put(domain, httpClient);
				}
			}
		}
		
		
		
		return httpClient;
	}
	
	
	public boolean login(final Request request, final Task task) {
		final Set<Integer> acceptStatCode = Sets.newHashSet(200, 302, 304);
		
		logger.info("login page {}", request.getUrl());
		try {
			final Site site = task.getSite();
			net.ion.radon.aclient.Request loginRequest = makeHttpUriRequest(request, site);

			NewClient nc = getHttpClient(site);
			
			return nc.executeRequest(loginRequest, new AsyncCompletionHandler<Boolean>(){
				@Override
				public Boolean onCompleted(Response response) throws Exception {
					int status = response.getStatusCode() ;
					request.putExtra(Request.STATUS_CODE, status) ;
					
					if (statusAccept(acceptStatCode, status)) {
						String charset = site.getCharset();
//						handleResponse(request, charset, response, task);
						List<Cookie> cookies = response.getCookies() ;
						cookiesPerDomain.put(site.getDomain(), cookies) ;
						return true;
					} else {
						logger.warn("code error " + status + "\t" + request.getUrl());
						return false;
					}
				}
				 public void onThrowable(Throwable t){
					 logger.warn("login page " + request.getUrl() + " error", t);
					 onError(request);
				 }
			}).get() ;
			
		} catch (Exception e) {
			logger.warn("login page " + request.getUrl() + " error", e);
			onError(request);
			e.printStackTrace(); 
			return false;
		} 
	}


	@Override
	public Page download(final Request request, final Task task) {
		final Site site = task.getSite();
		final Set<Integer> acceptStatCode =  site.getAcceptStatCode();
		Map<String, String> headers = site.getHeaders();

		logger.info("downloading page {}", request.getUrl());
		try {
			net.ion.radon.aclient.Request httpUriRequest = makeHttpUriRequest(request, site);
			return getHttpClient(site).executeRequest(httpUriRequest, new AsyncCompletionHandler<Page>(){
				@Override
				public Page onCompleted(Response response) throws Exception {
					int status = response.getStatusCode() ;
					request.putExtra(Request.STATUS_CODE, status) ;
					
					if (statusAccept(acceptStatCode, status)) {
						String charset = site.getCharset();
						Page page = handleResponse(request, charset, response, task);
						onSuccess(request);
						return page;
					} else {
						logger.warn("code error " + status + "\t" + request.getUrl());
						return null;
					}
				}
				 public void onThrowable(Throwable t){
					 logger.warn("login page " + request.getUrl() + " error", t);
					 onError(request);
					 t.printStackTrace(); 
				 }
			}).get() ;
			
		} catch (Exception e) {
			logger.warn("download page " + request.getUrl() + " error", e);
			onError(request);
			e.printStackTrace(); 
			return null;
		} 
	}

	@Override
	public void setThread(int thread) {
		httpClientGenerator.setPoolSize(thread);
	}

	protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
		return acceptStatCode.contains(statusCode);
	}

	protected net.ion.radon.aclient.Request makeHttpUriRequest(Request request, Site site) {
		RequestBuilder requestBuilder = new RequestBuilder().setUrl(request.getUrl()).setMethod(request.getMethod()) ;
		
		Map<String, String> headers = site.getHeaders();
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}

		if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
			HttpHost proxyHost = site.getHttpProxyFromPool();
			requestBuilder.setProxyServer(new ProxyServer(Protocol.valueOf(proxyHost.getSchemeName().toUpperCase()), proxyHost.getHostName(), proxyHost.getPort())) ;
			request.putExtra(Request.PROXY, proxyHost);
		}
		
		NameValuePair[] params = request.getParameters();
		if (params != null && params.length > 0) {
			for (NameValuePair pair : params) {
				requestBuilder.addParameter(pair.getName(), pair.getValue()) ;
			}
		}
		
		Realm realm = request.realm() ;
		if (realm != null) {
			requestBuilder.setRealm(realm) ;
		}
		
		
		
		
		List<Cookie> cookies = cookiesPerDomain.get(site.getDomain()) ;
		if (cookies != null){
			for (Cookie cookie : cookies) {
				requestBuilder.addCookie(cookie) ;
			}
		}

		net.ion.radon.aclient.Request result = requestBuilder.build();

		return result;
	}


	private Page handleResponse(Request request, String charset, Response httpResponse, Task task) throws IOException {
		String contentType = httpResponse.getHeader(HeaderConstant.HEADER_CONTENT_TYPE);
		Page page = new Page();
		if (contentType != null && (contentType.indexOf("text") > -1 || contentType.indexOf("json") > -1 || contentType.indexOf("xml") > -1)) {
			String content = getContent(charset, httpResponse);
			page.setRawText(content);
		} else {
			BinaryHandler bhandler = task.getSite().getBinaryHandler() ;
			page.setRawText("") ;
			if (bhandler != null && bhandler != BinaryHandler.BLANK ){
				Object result = bhandler.handle(request, httpResponse.getHeaders(), httpResponse.getBodyAsStream());
				page.putField(BinaryHandler.class.getCanonicalName(), result) ;
			}
		}

		page.setUrl(new PlainText(request.getUrl()));
		page.setRequest(request);
		page.setStatusCode(httpResponse.getStatusCode());
		
		return page;
	}


	private String getContent(String charset, Response httpResponse) throws IOException {
		if (charset == null) {
			byte[] contentBytes = IOUtils.toByteArray(httpResponse.getBodyAsStream());
			String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
			if (htmlCharset != null) {
				return new String(contentBytes, htmlCharset);
			} else {
				logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
				return new String(contentBytes);
			}
		} else {
			return IOUtils.toString(httpResponse.getBodyAsStream(), charset);
		}
	}

	private String getHtmlCharset(Response httpResponse, byte[] contentBytes) throws IOException {
		String charset;
		// charset
		// 1、encoding in http header Content-Type
		String value = httpResponse.getHeader(HeaderConstant.HEADER_CONTENT_TYPE);
		charset = UrlUtils.getCharset(value);
		if (StringUtil.isNotBlank(charset)) {
			logger.debug("Auto get charset: {}", charset);
			return charset;
		}
		// use default charset to decode first time
		Charset defaultCharset = Charset.defaultCharset();
		String content = new String(contentBytes, defaultCharset.name());
		// 2、charset in meta
		if (StringUtil.isNotEmpty(content)) {
			Document document = Jsoup.parse(content);
			Elements links = document.select("meta");
			for (Element link : links) {
				// 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				String metaContent = link.attr("content");
				String metaCharset = link.attr("charset");
				if (metaContent.indexOf("charset") != -1) {
					metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
					charset = metaContent.split("=")[1];
					break;
				}
				// 2.2、html5 <meta charset="UTF-8" />
				else if (StringUtil.isNotEmpty(metaCharset)) {
					charset = metaCharset;
					break;
				}
			}
		}
		logger.debug("Auto get charset: {}", charset);
		// 3、todo use tools as cpdetector for content decode
		return charset;
	}
}
