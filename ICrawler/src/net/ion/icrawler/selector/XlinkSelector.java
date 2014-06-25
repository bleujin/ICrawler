package net.ion.icrawler.selector;

import java.util.List;

import net.ion.icrawler.xsoup.Xsoup;
import net.ion.icrawler.xsoup.xevaluator.XPathEvaluator;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.nodes.Element;

public class XlinkSelector extends BaseElementSelector {

	private XPathEvaluator xPathEvaluator;

	public XlinkSelector(String xpathStr) {
		this.xPathEvaluator = Xsoup.compile(xpathStr);
	}

	@Override
	public String select(Element element) {
		return xPathEvaluator.evaluate(element).get();
	}

	@Override
	public List<String> selectList(Element element) {
		return xPathEvaluator.evaluate(element).list();
	}

	@Override
	public Element selectElement(Element element) {
		List<Element> elements = selectElements(element);
		if (CollectionUtils.isNotEmpty(elements)) {
			return elements.get(0);
		}
		return null;
	}

	@Override
	public List<Element> selectElements(Element element) {
		return xPathEvaluator.evaluate(element).getElements();
	}

	@Override
	public boolean hasAttribute() {
		return xPathEvaluator.hasAttribute();
	}
}

