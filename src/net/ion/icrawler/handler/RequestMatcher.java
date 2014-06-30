package net.ion.icrawler.handler;

import net.ion.icrawler.Request;

public interface RequestMatcher {

	/**
	 * Check whether to process the page.<br>
	 * </br> Please DO NOT change page status in this method.
	 * 
	 * @param page
	 * @return
	 */
	public boolean match(Request page);

	public enum MatchOther {
		YES, NO
	}
}
