package net.ion.icrawler.pipeline;

import java.util.List;

/**
 * Pipeline that can collect and store results. <br>
 * Used for {@link net.ion.icrawler.Spider#getAll(java.util.Collection)}
 */
public interface CollectorPipeline<T> extends Pipeline {

	/**
	 * Get all results collected.
	 * 
	 * @return collected results
	 */
	public List<T> getCollected();
}
