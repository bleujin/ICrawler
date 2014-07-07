package net.ion.icrawler.selector;

import static net.ion.icrawler.selector.Selectors.$;
import static net.ion.icrawler.selector.Selectors.and;
import static net.ion.icrawler.selector.Selectors.or;
import static net.ion.icrawler.selector.Selectors.regex;
import static net.ion.icrawler.selector.Selectors.xpath;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * <br>
 */
public class ExtractorsTest extends TestCase {

	String html = "<div><h1>test<a href=\"xxx\">aabbcc</a></h1></div>";
	String html2 = "<title>aabbcc</title>";

	public void testEach() {
		assertEquals("<a href=\"xxx\">aabbcc</a>", $("div h1 a").select(html));
		assertEquals("xxx", $("div h1 a", "href").select(html));
		assertEquals("aabbcc", $("div h1 a", "innerHtml").select(html));
		assertEquals("xxx", xpath("//a/@href").select(html));
		assertEquals("xxx", regex("a href=\"(.*)\"").select(html));
		assertEquals("xxx", regex("(a href)=\"(.*)\"", 2).select(html));
	}

	public void testCombo() {
		assertEquals("bb", and($("title"), regex("aa(bb)cc")).select(html2));
		OrSelector or = or($("div h1 a", "innerHtml"), xpath("//title"));
		assertEquals("aabbcc", or.select(html));
		assertEquals("<title>aabbcc</title>", or.select(html2));
	}
}
