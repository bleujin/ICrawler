package net.ion.icrawler.selector;

import junit.framework.TestCase;

import org.junit.Test;

public class JsonTest extends TestCase {

	private String text = "callback({\"name\":\"json\"})";

	private String textWithBrackerInContent = "callback({\"name\":\"json)\"})";

	@Test
	public void testRemovePadding() throws Exception {
		String name = new Json(text).removePadding("callback").jsonPath("$.name").get();
		assertEquals("json", name);
	}

	@Test
	public void testRemovePaddingForQuotes() throws Exception {
		String name = new Json(textWithBrackerInContent).removePadding("callback").jsonPath("$.name").get();
		assertEquals("json)", name);
	}
}
