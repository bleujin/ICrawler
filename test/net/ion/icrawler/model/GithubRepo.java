package net.ion.icrawler.model;

import net.ion.icrawler.Site;
import net.ion.icrawler.model.ConsolePageModelPipeline;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.HelpUrl;
import net.ion.icrawler.model.annotation.TargetUrl;

/**
 * <br>
 * 
 * @since 0.3.2
 */
@TargetUrl("https://github.com/\\w+/\\w+")
@HelpUrl({ "https://github.com/\\w+\\?tab=repositories", "https://github.com/\\w+", "https://github.com/explore/*" })
public class GithubRepo extends BaseRepo {

	@ExtractBy("//ul[@class='pagehead-actions']/li[2]//a[@class='social-count']/text()")
	private int fork;

	public static void main(String[] args) {
		OOSpider.create(Site.me().sleepTime(100), new ConsolePageModelPipeline(), GithubRepo.class).addUrl("https://github.com/bleujin").thread(10).run();
	}

	public int getStar() {
		return star;
	}

	public int getFork() {
		return fork;
	}
}
