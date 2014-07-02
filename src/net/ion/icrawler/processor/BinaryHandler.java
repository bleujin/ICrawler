package net.ion.icrawler.processor;

import java.io.InputStream;
import java.util.Map;

import net.ion.icrawler.Request;


public interface BinaryHandler<T> {

	public final static BinaryHandler BLANK = new BinaryHandler<Void>() {
		@Override
		public Void handle(Request request, Map headers, InputStream input) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public T handle(Request request, Map headers, InputStream input) ;
}
