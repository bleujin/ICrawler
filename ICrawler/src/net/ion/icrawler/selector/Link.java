package net.ion.icrawler.selector;

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
}
