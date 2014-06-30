package net.ion.icrawler.example;

import net.ion.icrawler.*;
import net.ion.icrawler.handler.CompositePageProcessor;
import net.ion.icrawler.handler.CompositePipeline;
import net.ion.icrawler.handler.PatternProcessor;
import net.ion.icrawler.handler.RequestMatcher;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA. User: Sebastian MA Date: April 04, 2014 Time: 21:23
 */
public class PatternProcessorExample {

	private static Logger log = Logger.getLogger(PatternProcessorExample.class);

	public static void main(String... args) {

		// define a patternProcessor which handles only "http://item.jd.com/.*"
		PatternProcessor githubRepoProcessor = new PatternProcessor("https://github\\.com/[\\w\\-]+/[\\w\\-]+") {

			@Override
			public RequestMatcher.MatchOther processPage(Page page) {
				page.putField("reponame", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
				return RequestMatcher.MatchOther.YES;
			}

			@Override
			public RequestMatcher.MatchOther processResult(ResultItems resultItems, Task task) {
				log.info("Extracting from repo" + resultItems.getRequest());
				System.out.println("Repo name: " + resultItems.asObject("reponame"));
				return RequestMatcher.MatchOther.YES;
			}
		};

		PatternProcessor githubUserProcessor = new PatternProcessor("https://github\\.com/[\\w\\-]+") {

			@Override
			public RequestMatcher.MatchOther processPage(Page page) {
				log.info("Extracting from " + page.getUrl());
				page.addRequests(page.getHtml().links().regex("https://github\\.com/[\\w\\-]+/[\\w\\-]+").all());
				page.addRequests(page.getHtml().links().regex("https://github\\.com/[\\w\\-]+").all());
				page.putField("username", page.getHtml().xpath("//span[@class='vcard-fullname']/text()").toString());
				return RequestMatcher.MatchOther.YES;
			}

			@Override
			public RequestMatcher.MatchOther processResult(ResultItems resultItems, Task task) {
				System.out.println("User name: " + resultItems.asObject("username"));
				return RequestMatcher.MatchOther.YES;
			}
		};
		
		

		CompositePageProcessor pageProcessor = new CompositePageProcessor();
		CompositePipeline pipeline = new CompositePipeline();

		pageProcessor.setSubPageProcessors(githubRepoProcessor, githubUserProcessor);
		pipeline.setSubPipeline(githubRepoProcessor, githubUserProcessor);

		
		Site.create("http://github.com").setRetryTimes(3).newSpider(pageProcessor).addUrl("https://github.com/bleujin").thread(5).addPipeline(pipeline).runAsync();
	}

}
