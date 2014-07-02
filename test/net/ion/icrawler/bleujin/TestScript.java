package net.ion.icrawler.bleujin;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;

public class TestScript extends TestCase {


	private ScriptEngine sengine;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ScriptEngineManager manager = new ScriptEngineManager();
		this.sengine = manager.getEngineByName("JavaScript");
	}
	
	public void testBinding() throws Exception {
		String script = "print('Hello ' + name) ;" ;
		Bindings binds = new SimpleBindings();
		binds.put("name", "bleujin") ;
		sengine.eval(script, binds) ;
	}

	public void testRunWithScript() throws Exception {
		String script = IOUtil.toStringWithClose(getClass().getResourceAsStream("crawler.script"));
		Object pack = sengine.eval(script);

		Object result = ((Invocable) sengine).invokeMethod(pack, "handle");
	}
	
	public void testRunWithScript2() throws Exception {
		String script = IOUtil.toStringWithClose(getClass().getResourceAsStream("crawler_tistory.script"));
		Object pack = sengine.eval(script);

		Object result = ((Invocable) sengine).invokeMethod(pack, "handle");
	}

}
