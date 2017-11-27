package net.ion.icrawler;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;

public class PageContent {

	private String rawText;
	private String url;

	public PageContent(String rawText, String url) {
		this.rawText = rawText ;
		this.url = url ;
	}

	public String extractArticle() {
		try {
			return ArticleExtractor.INSTANCE.getText(rawText);
		} catch (BoilerpipeProcessingException e) {
			throw new IllegalStateException(e) ;
		}
	}

	
	public String extractDefault() {
		try {
			return DefaultExtractor.INSTANCE.getText(rawText);
		} catch (BoilerpipeProcessingException e) {
			throw new IllegalStateException(e) ;
		}
	}
	
	public String extractLargest() {
		try {
			return LargestContentExtractor.INSTANCE.getText(rawText);
		} catch (BoilerpipeProcessingException e) {
			throw new IllegalStateException(e) ;
		}
	}
	
	public String rawText() {
		return rawText ;
	}
	
}
