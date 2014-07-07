package net.ion.icrawler.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.ion.radon.aclient.ProxyServer.Protocol;

public class HttpHost {

	
	private InetAddress address;
	private int port;
	private Protocol protocol = Protocol.HTTP;

	public HttpHost(Protocol protocol, InetAddress address, int port) {
		this.protocol = protocol ;
		this.address = address ;
		this.port = port ;
	}

	public static HttpHost createByHost(Protocol protocol, String host, int port) throws UnknownHostException{
		return new HttpHost(protocol, InetAddress.getByName(host), port) ;
	}

	public static HttpHost createByHost(String host, int port) throws UnknownHostException{
		return new HttpHost(Protocol.HTTP, InetAddress.getByName(host), port) ;
	}


	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getHostName() {
		return getAddress().getHostName();
	}

	public Protocol getProtocol() {
		return this.protocol ;
	}

}
