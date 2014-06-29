ICrawler
========

// This project is an adaptation of the code4craft

crawler with script


<pre>public class TestFirst extends TestCase {

	public void testSimple() throws Exception {
		Site site = Site.create("http://bleujin.tistory.com").sleepTime(50) ;
		
		Spider spider = site.createSpider(new SimplePageProcessor("http://bleujin.tistory.com/\\d+"))
				.startUrls("http://bleujin.tistory.com/").scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		
		spider.addPipeline(new DebugPipeline()).run();
	}

	public void testRunWithScript() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine sengine = manager.getEngineByName("JavaScript");
		
		String script = IOUtil.toStringWithClose(getClass().getResourceAsStream("crawler.script"));
		Object pack = sengine.eval(script) ;
		
		Object result = ((Invocable) sengine).invokeMethod(pack, "handle");
	}
}
</pre>