package net.ion.icrawler.model;

import net.ion.icrawler.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 */
public interface AfterExtractor {

	public void afterProcess(Page page);
}
