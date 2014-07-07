package net.ion.icrawler.selector;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class SelectorTest extends TestCase{

	private String html = "<div><a href='http://whatever.com/aaa'></a></div><div><a href='http://whatever.com/bbb'></a></div>";

	@Test
	public void testChain() throws Exception {
		Html selectable = new Html(html);
		List<String> linksWithoutChain = selectable.links().all();
		Selectable xpath = selectable.xpath("//div");
		List<String> linksWithChainFirstCall = xpath.links().all();
		List<String> linksWithChainSecondCall = xpath.links().all();
		assertTrue(linksWithoutChain.size() == linksWithChainFirstCall.size());
		assertTrue(linksWithChainFirstCall.size() == linksWithChainSecondCall.size());
	}

	@Test
	public void testNodes() throws Exception {
		Html selectable = new Html(html);
		List<Selectable> links = selectable.xpath("//a").nodes();
		assertEquals("http://whatever.com/aaa", links.get(0).links().get());
	}
}
