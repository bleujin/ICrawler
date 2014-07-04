package net.ion.jci.bleujin;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectUtil;
import net.ion.jci.ReloadingClassLoader;
import net.ion.jci.compilers.CompilationResult;
import net.ion.jci.compilers.JaninoJavaCompiler;
import net.ion.jci.compilers.JavaCompiler;
import net.ion.jci.readers.ResourceReader;
import net.ion.jci.stores.MemoryResourceStore;

public class TestCompiler extends TestCase {

	
	public void testSimpleCompile() throws Exception {
		final JavaCompiler compiler = new JaninoJavaCompiler();

		final MemoryResourceStore store = new MemoryResourceStore();
		final CompilationResult result = compiler.compile(new String[] { "jci/Simple.java" }, new MyReourceReader(), store);

		assertEquals(ObjectUtil.toString(result.getErrors()), 0, result.getErrors().length);
		assertEquals(ObjectUtil.toString(result.getWarnings()), 0, result.getWarnings().length);

		final byte[] clazzBytes = store.read("jci/Simple.class");
		assertNotNull("jci/Simple.class is not null", clazzBytes);
		assertTrue("jci/Simple.class is not empty", clazzBytes.length > 0);
	}

	
	public void testReloadClass() throws Exception {
		ReloadingClassLoader rc = new ReloadingClassLoader(getClass().getClassLoader()) ;

		final MemoryResourceStore store = new MemoryResourceStore();
		rc.addResourceStore(store) ;
		
		
		final JavaCompiler compiler = new JaninoJavaCompiler();

		final CompilationResult result = compiler.compile(new String[] { "jci/Simple.java" }, new MyReourceReader(), store);
		assertEquals(ObjectUtil.toString(result.getErrors()), 0, result.getErrors().length);
		
		Class<?> clz = rc.loadClass("jci.Simple") ;
		Object obj = clz.newInstance() ;
		Debug.line(obj);
	}


	
	@SuppressWarnings("unused")
	private static class MyReourceReader implements ResourceReader{
		final private Map<String, byte[]> sources = new HashMap<String, byte[]>() {
			private static final long serialVersionUID = 1L;
			{
				put("jci/Simple.java", 
					("package jci;\n" 
						+ "public class Simple {\n" 
						+ "  public String toString() {\n" 
						+ "    return \"Simple\";\n" 
						+ "  }\n" 
						+ "}").getBytes());
			}
		};

		public byte[] getBytes(final String pResourceName) {
			return sources.get(pResourceName);
		}

		public boolean isAvailable(final String pResourceName) {
			return sources.containsKey(pResourceName);
		}
	}
	
}
