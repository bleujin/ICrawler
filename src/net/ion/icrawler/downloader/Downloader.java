package net.ion.icrawler.downloader;

import net.ion.icrawler.Page;
import net.ion.icrawler.Request;
import net.ion.icrawler.Task;

/**
 * Downloader is the part that downloads web pages and store in Page object. <br>
 * Downloader has {@link #setThread(int)} method because downloader is always the bottleneck of a crawler, there are always some mechanisms such as pooling in downloader, and pool size is related to thread numbers.
 */
public interface Downloader {

	public boolean login(Request loginRequest, Task task);

	/**
	 * Downloads web pages and store in Page object.
	 */
	public Page download(Request request, Task task);

	/**
	 * Tell the downloader how many threads the spider used.
	 */
	public void setThread(int threadNum);

}
