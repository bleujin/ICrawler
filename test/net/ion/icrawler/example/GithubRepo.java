package net.ion.icrawler.example;

import java.util.List;

import net.ion.icrawler.Site;
import net.ion.icrawler.model.ConsolePageModelPipeline;
import net.ion.icrawler.model.HasKey;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.ExtractByUrl;
import net.ion.icrawler.model.annotation.HelpUrl;
import net.ion.icrawler.model.annotation.TargetUrl;

@TargetUrl("https://github.com/\\w+/\\w+")
@HelpUrl({ "https://github.com/\\w+\\?tab=repositories", "https://github.com/\\w+", "https://github.com/explore/*" })
public class GithubRepo implements HasKey {

	@ExtractBy(value = "//h1[@class='entry-title public']/strong/a/text()", notNull = true)
	private String name;

	@ExtractByUrl("https://github\\.com/(\\w+)/.*")
	private String author;

	@ExtractBy("//div[@id='readme']/tidyText()")
	private String readme;

	@ExtractBy(value = "//div[@class='repository-lang-stats']//li//span[@class='lang']/text()", multi = true)
	private List<String> language;

	@ExtractBy("//ul[@class='pagehead-actions']/li[1]//a[@class='social-count js-social-count']/text()")
	private int star;

	@ExtractBy("//ul[@class='pagehead-actions']/li[2]//a[@class='social-count']/text()")
	private int fork;

	@ExtractByUrl
	private String url;

	public static void main(String[] args) {
		Site.me().sleepTime(100).createOOSpider(new ConsolePageModelPipeline(), GithubRepo.class).addUrl("https://github.com/bleujin").thread(10).run();
	}

	@Override
	public String key() {
		return author + ":" + name;
	}

	public String getName() {
		return name;
	}

	public String getReadme() {
		return readme;
	}

	public String getAuthor() {
		return author;
	}

	public List<String> getLanguage() {
		return language;
	}

	public String getUrl() {
		return url;
	}

	public int getStar() {
		return star;
	}

	public int getFork() {
		return fork;
	}

	@Override
	public String toString() {
		return "GithubRepo{" + "name='" + name + '\'' + ", author='" + author + '\'' + ", readme='" + readme + '\'' + ", language=" + language + ", star=" + star + ", fork=" + fork + ", url='" + url + '\'' + '}';
	}
}
