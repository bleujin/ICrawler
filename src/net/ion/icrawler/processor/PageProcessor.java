package net.ion.icrawler.processor;

import net.ion.icrawler.Page;
import net.ion.icrawler.Site;

/**
 * Interface to be implemented to customize a crawler.<br>
 * <br>
 * In PageProcessor, you can customize:
 * <p/>
 * start urls and other settings in {@link Site}<br>
 * how the urls to fetch are detected <br>
 * how the data are extracted and stored <br>
 * 
 * <br>
 * 
 * @see Site
 * @see Page
 * @since 0.1.0
 */
public interface PageProcessor {
	
	public void process(Page page);

}
