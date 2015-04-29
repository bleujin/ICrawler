package net.ion.icrawler.selector;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.ListUtil;

import org.apache.commons.lang.StringUtils;

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
		return selectLink(regexSelector, targets);
	}

	@Override
	public PlainLink regex(String regex, int group) {
		RegexSelector regexSelector = Selectors.regex(regex, group);
		return selectLink(regexSelector, targets);
	}
	
	protected PlainLink selectLink(Selector selector, List<Link> linkElements) {
		List<Link> results = ListUtil.newList() ;
		for (Link ele : linkElements) {
			String result = selector.select(ele.target());
			if (StringUtils.isNotBlank(result)) results.add(ele);
		}
		return new PlainLink(results);
	}
	
	@Override
	protected PlainLink selectList(Selector selector, List<String> linkElements) {
		List<Link> results = ListUtil.newList() ;
		for (String ele : linkElements) {
			String result = selector.select(ele);
			if (StringUtils.isNotBlank(result)) results.add(new Link(ele, ""));
		}
		return new PlainLink(results);
	}
	
	@Override
	public Selectable links() {
		throw new UnsupportedOperationException();
	}

	public PlainLink matches(String regex){
		List<Link> result = ListUtil.newList() ; 
		for(Link link : targets){
			if (link.target().matches(regex)) result.add(link) ;
		}
		return new PlainLink(result) ;
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
