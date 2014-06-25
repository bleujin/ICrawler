package net.ion.icrawler.selector;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

import org.jsoup.nodes.Element;

public class PlainLink extends AbstractSelectable {

	protected List<Link> targets;

	public PlainLink(List<Link> elements) {
		this.targets = elements;
	}

	@Override
	public Selectable xpath(String xpath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Selectable $(String selector) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Selectable $(String selector, String attrName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Selectable smartContent() {
		throw new UnsupportedOperationException();
	}


	@Override
	public PlainLink regex(String regex) {
		RegexSelector regexSelector = Selectors.regex(regex);
		return selectList(regexSelector, targets);
	}

	@Override
	public PlainLink regex(String regex, int group) {
		RegexSelector regexSelector = Selectors.regex(regex, group);
		return selectList(regexSelector, targets);
	}
	
	protected PlainLink selectList(Selector selector, List<Link> linkElements) {
		List<Link> results = ListUtil.newList() ;
		for (Link ele : linkElements) {
			String result = selector.select(ele.target());
			if (StringUtil.isNotBlank(result)) results.add(ele);
		}
		return new PlainLink(results);
	}
	
	@Override
	public Selectable links() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Selectable> nodes() {
		List<Selectable> nodes = new ArrayList<Selectable>(getSourceTexts().size());
		for (String string : getSourceTexts()) {
			nodes.add(PlainText.create(string));
		}
		return nodes;
	}

	@Override
	protected List<String> getSourceTexts() {
		List<String> result = ListUtil.newList() ;
		for (Link link : targets) {
			result.add(link.target()) ; 
		}
		return result;
	}

	public List<Link> targets() {
		return targets;
	}
}
