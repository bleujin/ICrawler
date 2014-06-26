package net.ion.icrawler.downloader;

import net.ion.icrawler.Spider;
import net.ion.icrawler.downloader.FileCache;

import org.junit.Ignore;
import org.junit.Test;

/**
 * <br>
 */
public class FileCacheTest {

	@Ignore("takes long")
	@Test
	public void test() {
		FileCache fileCache = new FileCache("http://my.oschina.net/flashsword/blog", "http://my.oschina.net/flashsword/blog/*");
		Spider.create(fileCache).setDownloader(fileCache).addPipeline(fileCache).run();
	}
}
