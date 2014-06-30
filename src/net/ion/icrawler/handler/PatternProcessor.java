package net.ion.icrawler.handler;

public abstract class PatternProcessor extends PatternRequestMatcher implements SubPipeline, SubPageProcessor {
	public PatternProcessor(String pattern) {
		super(pattern);
	}
}
