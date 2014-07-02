package net.ion.icrawler.downloader;

import net.ion.icrawler.Site;
import net.ion.radon.aclient.Realm;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClientGenerator {

	private PoolingHttpClientConnectionManager connectionManager;

	public HttpClientGenerator() {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
		connectionManager = new PoolingHttpClientConnectionManager(reg);
		connectionManager.setDefaultMaxPerRoute(100);
	}

	public HttpClientGenerator setPoolSize(int poolSize) {
		connectionManager.setMaxTotal(poolSize);
		return this;
	}

	public CloseableHttpClient getClient(Site site) {
		return generateClient(site);
	}

	private CloseableHttpClient generateClient(Site site) {
		HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connectionManager);
		if (site != null && site.getUserAgent() != null) {
			builder.setUserAgent(site.getUserAgent());
		} else {
			builder.setUserAgent("");
		}
		if (site == null || site.isUseGzip()) {
			builder.addInterceptorFirst(new HttpRequestInterceptor() {

				public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept-Encoding", "gzip");
					}

				}
			});
		}
		SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
		builder.setDefaultSocketConfig(socketConfig);
		if (site != null) {
			builder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
		}
		generateCookie(builder, site);
		
		
		
		if (site.loginRequest() != null && site.loginRequest().realm() != null){
			Realm realm = site.loginRequest().realm() ;
			
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(realm.getPrincipal(), realm.getPassword()));
			builder.setDefaultCredentialsProvider(credsProvider) ;

		}

		
		wrapClient(builder);
		CloseableHttpClient result = builder.build();

		return result ;
		
	}

	private void wrapClient(HttpClientBuilder builder) {
	    try {
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        X509TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        ctx.init(null, new TrustManager[]{tm}, null);
	        
	        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        builder.setSSLSocketFactory(ssf) ;
	        builder.setSslcontext(ctx) ;
	        builder.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER) ;
//	        builder.setSchemePortResolver(new DefaultSchemePortResolver()) ;
	        
//	        ClientConnectionManager ccm = builder.getConnectionManager();
//	        SchemeRegistry sr = ccm.getSchemeRegistry();
//	        sr.register(new Scheme("https", ssf, 443));
//	        
//	        return new DefaultHttpClient(ccm, builder.getParams());
	    } catch (Exception ex) {
	        throw new IllegalStateException(ex) ;
	    }
	}
	
	
	private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
		CookieStore cookieStore = new BasicCookieStore();
		for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
			BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
			cookie.setDomain(site.getDomain());
			cookieStore.addCookie(cookie);
		}
		for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
			for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
				cookie.setDomain(domainEntry.getKey());
				cookieStore.addCookie(cookie);
			}
		}
		httpClientBuilder.setDefaultCookieStore(cookieStore);
	}

}
