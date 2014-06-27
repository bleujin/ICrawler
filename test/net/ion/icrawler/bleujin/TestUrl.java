package net.ion.icrawler.bleujin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.data.Method;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.Realm.RealmBuilder;
import junit.framework.TestCase;

public class TestUrl extends TestCase{

	
	public void testSocket() throws Exception {
		
		Socket socket = new Socket(InetAddress.getByName("www.daum.net"), 80) ;
		OutputStream output = socket.getOutputStream() ;

		output.write("GET / HTTP/1.0\n".getBytes());
		output.write("Host: www.daum.net\n\n".getBytes());
		output.flush(); 
		
		InputStream input = socket.getInputStream() ;
		String content = IOUtil.toStringWithClose(input) ;
		
		Debug.line(content);
		
		socket.close(); 
	}
	
	
	public void testUrl() throws Exception {
		URL url = new URL("http://www.daum.net/") ;
		InputStream input = url.openStream() ;
		String content = IOUtil.toStringWithClose(input) ;
		Debug.line(content);
	}
	
	
	public void testParse() throws Exception {
		Connection connect = Jsoup.connect("http://www.daum.net/");
		connect.followRedirects(true) ;
		
		Document doc = connect.get();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
            System.out.println(String.format("* %s : %s", link.attr("abs:href"), link.ownText()));
        }
	}
	
	
}