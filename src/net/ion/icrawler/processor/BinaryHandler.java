package net.ion.icrawler.processor;

import java.io.InputStream;

import net.ion.icrawler.Request;

import org.apache.commons.collections.map.MultiValueMap;

public interface BinaryHandler<T> {

	public final static BinaryHandler BLANK = new BinaryHandler<Void>() {
		@Override
		public Void handle(Request request, MultiValueMap headers, InputStream input) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public T handle(Request request, MultiValueMap headers, InputStream input) ;
}
