package net.ion.icrawler.selector;

import junit.framework.TestCase;

import org.junit.Test;

public class RegexSelectorTest extends TestCase {

//	@Test(expected = IllegalArgumentException.class)
	public void testRegexWithSingleLeftBracket() {
		try {
			String regex = "\\d+(";
			new RegexSelector(regex);
			fail();
		} catch (IllegalArgumentException expect) {

		}
	}

	@Test
	public void testRegexWithLeftBracketQuoted() {
		String regex = "\\(.+";
		String source = "(hello world";
		RegexSelector regexSelector = new RegexSelector(regex);
		String select = regexSelector.select(source);
		assertEquals(source, select);
	}
}
