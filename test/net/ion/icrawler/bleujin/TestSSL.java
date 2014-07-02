package net.ion.icrawler.bleujin;

import java.io.InputStream;
import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class TestSSL extends TestCase {
	
	static HttpClient wrapClient(CloseableHttpClient base) {
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
	        ClientConnectionManager ccm = base.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, base.getParams());
	    } catch (Exception ex) {
	        throw new IllegalStateException(ex) ;
	    }
	}
	
	public void testSSL() throws Exception {
		DefaultHttpClient baseClient = new DefaultHttpClient();
		baseClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials("bleujin", "redfpark"));
		
		HttpClient httpClient = wrapClient(baseClient );
		
		HttpHost host = new HttpHost("im.i-on.net");

		HttpGet httpGet = new HttpGet("https://im.i-on.net/zeroboard/?s_url=/zeroboard/main.php") ;
		String encoding = DatatypeConverter.printBase64Binary("bleujin:redfpark".getBytes("UTF-8"));
		httpGet.setHeader("Authorization", "Basic " + encoding);
		
		HttpResponse response = httpClient.execute(host, httpGet) ;
		String cv = response.getFirstHeader("Set-Cookie").getValue() ;
		
		Debug.line(response.getStatusLine().getStatusCode()) ;
		InputStream input = response.getEntity().getContent() ;
		Debug.line(IOUtil.toStringWithClose(input)) ;
		response.getEntity().consumeContent(); 
		
		
		httpGet = new HttpGet("https://im.i-on.net/zeroboard/main.php") ;
		httpGet.addHeader("Set-Cookie", cv); 
		response = httpClient.execute(host, httpGet) ;

		input = response.getEntity().getContent() ;
		Debug.line(IOUtil.toStringWithClose(input)) ;
		response.getEntity().consumeContent(); 

	}
	
}
