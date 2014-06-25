package net.ion.bleujin;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;
import net.ion.icrawler.Spider;
import net.ion.icrawler.pipeline.DebugPipeline;
import net.ion.icrawler.processor.SimplePageProcessor;
import net.ion.icrawler.scheduler.MaxLimitScheduler;
import net.ion.icrawler.scheduler.QueueScheduler;

public class TestFirst extends TestCase {

	public void testSimple() throws Exception {
		SimplePageProcessor processor = new SimplePageProcessor("http://bleujin.tistory.com", "http://bleujin.tistory.com/*");
		Spider spider = Spider.create(processor).scheduler(new MaxLimitScheduler(new QueueScheduler(), 10));
		spider.getSite().setSleepTime(50);

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
