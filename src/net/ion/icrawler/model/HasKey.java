package net.ion.icrawler.model;

import net.ion.icrawler.utils.Experimental;

/**
 * Interface to be implemented by page mode.<br>
 * Can be used to identify a page model, or be used as name of file storing the object.<br>
 * <br>
 * 
 * @since 0.2.0
 */
@Experimental
public interface HasKey {

	/**
	 * 
	 * 
	 * @return key
	 */
	public String key();
}
