package net.ion.icrawler.example;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.icrawler.Site;
import net.ion.icrawler.model.ConsolePageModelPipeline;
import net.ion.icrawler.model.HasKey;
import net.ion.icrawler.model.OOSpider;
import net.ion.icrawler.model.annotation.ExtractBy;
import net.ion.icrawler.model.annotation.ExtractByUrl;

public class GithubRepoApi implements HasKey {

	@ExtractBy(type = ExtractBy.Type.JsonPath, value = "$.name")
	private String name;

	@ExtractBy(type = ExtractBy.Type.JsonPath, value = "$..owner.login")
	private String author;

	@ExtractBy(type = ExtractBy.Type.JsonPath, value = "$.language", multi = true)
	private List<String> language;

	@ExtractBy(type = ExtractBy.Type.JsonPath, value = "$.stargazers_count")
	private int star;

	@ExtractBy(type = ExtractBy.Type.JsonPath, value = "$.homepage")
	private int fork;

	@ExtractByUrl
	private String url;

	public static void main(String[] args) {
		GithubRepoApi api = Site.create().sleepTime(100).createOOSpider(new ConsolePageModelPipeline(), GithubRepoApi.class).<GithubRepoApi> get("https://api.github.com/repos/bleujin/ICrawler");
		
		Debug.line(api.getAuthor());
		
		// Site.me().sleepTime(100).createOOSpider(new ConsolePageModelPipeline(), GithubRepoApi.class).addUrl("https://api.github.com/repos/bleujin/ICrawler").run();
		// OOSpider.create(Site.me().sleepTime(100), new ConsolePageModelPipeline(), GithubRepoApi.class).addUrl("https://api.github.com/repos/bleujin/ICrawler").run();
	}

	@Override
	public String key() {
		return author + ":" + name;
	}

	public String getName() {
		return name;
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
}
