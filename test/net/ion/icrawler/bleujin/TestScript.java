package net.ion.icrawler.bleujin;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;

public class TestScript extends TestCase {

	public void testRunWithScript() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine sengine = manager.getEngineByName("JavaScript");

		String script = IOUtil.toStringWithClose(getClass().getResourceAsStream("crawler.script"));
		Object pack = sengine.eval(script);

		Object result = ((Invocable) sengine).invokeMethod(pack, "handle");
	}

	public void testRunWithScript2() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine sengine = manager.getEngineByName("JavaScript");

		String script = IOUtil.toStringWithClose(getClass().getResourceAsStream("crawler_tistory.script"));
		Object pack = sengine.eval(script);

		Object result = ((Invocable) sengine).invokeMethod(pack, "handle");
	}

}
