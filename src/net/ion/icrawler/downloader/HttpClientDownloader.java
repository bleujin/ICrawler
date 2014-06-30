package net.ion.icrawler.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ion.framework.util.ObjectUtil;
import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.Site;
import net.ion.icrawler.Task;
import net.ion.icrawler.processor.BinaryHandler;
import net.ion.icrawler.selector.PlainText;
import net.ion.icrawler.utils.UrlUtils;
import net.ion.radon.aclient.FluentStringsMap;
import net.ion.radon.aclient.simple.HeaderConstant;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The http downloader based on HttpClient.
 */
@ThreadSafe
public class HttpClientDownloader extends AbstractDownloader {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

	private CloseableHttpClient getHttpClient(Site site) {
		if (site == null) {
			return httpClientGenerator.getClient(null);
		}
		String domain = site.getDomain();
		CloseableHttpClient httpClient = httpClients.get(domain);
		if (httpClient == null) {
			synchronized (this) {
				httpClient = httpClients.get(domain);
				if (httpClient == null) {
					httpClient = httpClientGenerator.getClient(site);
					httpClients.put(domain, httpClient);
				}
			}
		}
		return httpClient;
	}

	@Override
	public Page download(Request request, Task task) {
		Site site = null;
		if (task != null) {
			site = task.getSite();
		}
		Set<Integer> acceptStatCode;
		String charset = null;
		Map<String, String> headers = null;
		if (site != null) {
			acceptStatCode = site.getAcceptStatCode();
			charset = site.getCharset();
			headers = site.getHeaders();
		} else {
			acceptStatCode = Sets.newHashSet(200);
		}
		logger.info("downloading page {}", request.getUrl());
		CloseableHttpResponse httpResponse = null;
		int statusCode = 0;
		try {
			HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);

			httpResponse = getHttpClient(site).execute(httpUriRequest);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			request.putExtra(Request.STATUS_CODE, statusCode);
			if (statusAccept(acceptStatCode, statusCode)) {
				Page page = handleResponse(request, charset, httpResponse, task);
				onSuccess(request);
				return page;
			} else {
				logger.warn("code error " + statusCode + "\t" + request.getUrl());
				return null;
			}
		} catch (IOException e) {
			logger.warn("download page " + request.getUrl() + " error", e);
			if (site.getCycleRetryTimes() > 0) {
				return addToCycleRetry(request, site);
			}
			onError(request);
			e.printStackTrace(); 
			return null;
		} finally {
			request.putExtra(Request.STATUS_CODE, statusCode);
			try {
				if (httpResponse != null) {
					// ensure the connection is released back to pool
					EntityUtils.consume(httpResponse.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
	}

	@Override
	public void setThread(int thread) {
		httpClientGenerator.setPoolSize(thread);
	}

	protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
		return acceptStatCode.contains(statusCode);
	}

	protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {

		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut()).setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.BEST_MATCH);
		if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
			HttpHost proxyHost = site.getHttpProxyFromPool();
//			HttpHost proxyHost = new HttpHost("127.0.0.1", 8118, "http") ;
			requestConfigBuilder.setProxy(proxyHost);
			request.putExtra(Request.PROXY, proxyHost);
		}
		requestBuilder.setConfig(requestConfigBuilder.build());

		HttpUriRequest result = requestBuilder.build();

		NameValuePair[] params = request.getParameters();
		if (params != null && params.length > 0) {
			BasicHttpParams bp = new BasicHttpParams(); // add Parameter
			for (NameValuePair pair : params) {
				bp.setParameter(pair.getName(), pair.getValue());
			}
			result.setParams(bp);
		}
		return result;
	}

	protected RequestBuilder selectRequestMethod(Request request) {
		Method method = request.getMethod();
		if (method == null || method == Method.GET) {
			// default get
			return RequestBuilder.get();
		} else if (method.equals(Method.POST)) {
			RequestBuilder requestBuilder = RequestBuilder.post();
			requestBuilder.addParameters(request.getParameters());
			return requestBuilder;
		} else if (method.equals(Method.HEAD)) {
			return RequestBuilder.head();
		} else if (method.equals(Method.PUT)) {
			return RequestBuilder.put();
		} else if (method.equals(Method.DELETE)) {
			return RequestBuilder.delete();
		} else if (method.equals(Method.TRACE)) {
			return RequestBuilder.trace();
		}
		throw new IllegalArgumentException("Illegal HTTP Method " + method);
	}

	protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
		Header contentType = httpResponse.getFirstHeader(HeaderConstant.HEADER_CONTENT_TYPE);
		
		Page page = new Page();
		if (contentType.getValue().indexOf("text") > -1 || contentType.getValue().indexOf("json") > -1 || contentType.getValue().indexOf("xml") > -1) {
			String content = getContent(charset, httpResponse);
			page.setRawText(content);
		} else {
			BinaryHandler bhandler = task.getSite().getBinaryHandler() ;
			page.setRawText("") ;
			if (bhandler != null && bhandler != BinaryHandler.BLANK ){
				Object result = bhandler.handle(request, getHeaders(httpResponse.getAllHeaders()), httpResponse.getEntity().getContent());
				page.putField(BinaryHandler.class.getCanonicalName(), result) ;
			}
		}

		page.setUrl(new PlainText(request.getUrl()));
		page.setRequest(request);
		page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		return page;
	}

	private MultiValueMap getHeaders(Header[] headers) {
		MultiValueMap result = new MultiValueMap() ;
		for (Header header : headers) {
			result.put(header.getName(), header.getValue()) ;
		} 
		
		return result;
	}

	protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
		if (charset == null) {
			byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
			String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
			if (htmlCharset != null) {
				return new String(contentBytes, htmlCharset);
			} else {
				logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
				return new String(contentBytes);
			}
		} else {
			return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
		}
	}

	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
		String charset;
		// charset
		// 1、encoding in http header Content-Type
		String value = httpResponse.getEntity().getContentType().getValue();
		charset = UrlUtils.getCharset(value);
		if (StringUtils.isNotBlank(charset)) {
			logger.debug("Auto get charset: {}", charset);
			return charset;
		}
		// use default charset to decode first time
		Charset defaultCharset = Charset.defaultCharset();
		String content = new String(contentBytes, defaultCharset.name());
		// 2、charset in meta
		if (StringUtils.isNotEmpty(content)) {
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
				else if (StringUtils.isNotEmpty(metaCharset)) {
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
