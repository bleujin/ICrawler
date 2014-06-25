package net.ion.icrawler;

import net.ion.icrawler.selector.Html;

import org.junit.Test;

/**
 * <br>
 * Date: 13-4-21 Time: 上午8:42
 */
public class HtmlTest {

	@Test
	public void testRegexSelector() {
		Html selectable = new Html("aaaaaaab");
		// Assert.assertEquals("abbabbab",
		// (selectable.regex("(.*)").replace("aa(a)", "$1bb").toString()));
		System.out.println(selectable.regex("(.*)").replace("aa(a)", "$1bb").toString());

	}

}
