package net.ion.icrawler.xsoup.xevaluator;

import org.jsoup.nodes.Element;


public interface XPathEvaluator {

	XElements evaluate(Element element);

	boolean hasAttribute();

}
