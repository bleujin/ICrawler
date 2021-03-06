package net.ion.icrawler.xsoup;

import net.ion.icrawler.xsoup.w3c.NodeAdaptors;
import net.ion.icrawler.xsoup.xevaluator.XElements;
import net.ion.icrawler.xsoup.xevaluator.XPathEvaluator;
import net.ion.icrawler.xsoup.xevaluator.XPathParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author code4crafter@gmail.com
 */
public class Xsoup {

	/*-------------     XEvaluator         --------------- */

	public static XElements select(Element element, String xpathStr) {
		return XPathParser.parse(xpathStr).evaluate(element);
	}

	public static XElements select(String html, String xpathStr) {
		return XPathParser.parse(xpathStr).evaluate(Jsoup.parse(html));
	}

	public static XPathEvaluator compile(String xpathStr) {
		return XPathParser.parse(xpathStr);
	}

	/*-------------     W3cAdaptor         --------------- */

	public static org.w3c.dom.Element convertElement(Element element) {
		return NodeAdaptors.getElement(element);
	}

	public static org.w3c.dom.Document convertDocument(Document document) {
		return NodeAdaptors.getDocument(document);
	}

}
