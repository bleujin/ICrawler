package net.ion.icrawler;

import junit.framework.TestCase;

/**

 */
public class ResultItemsTest extends TestCase  {

	public void testOrderOfEntries() throws Exception {
		ResultItems resultItems = new ResultItems();
		resultItems.put("a", "a");
		resultItems.put("b", "b");
		resultItems.put("c", "c");
		assertTrue(resultItems.getAll().keySet().contains("a"));
		assertTrue(resultItems.getAll().keySet().contains("b"));
		assertTrue(resultItems.getAll().keySet().contains("c"));

	}
}
