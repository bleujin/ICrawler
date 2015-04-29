package net.ion.icrawler.selector;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Link {
	private String target ;
	private String anchor ;
	
	public Link(String target, String anchor){
		this.target = target ;
		this.anchor = anchor ;
	}
	
	public String target(){
		return target ;
	}
	
	public String anchor(){
		return anchor ;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this) ;
	}
}
