package net.ion.jci.cloader;

import net.ion.framework.util.Debug;

import org.apache.commons.lang.reflect.MethodUtils;

import junit.framework.TestCase;

public class TestDirClassLoader extends TestCase {

	public void testJar() throws Exception {
		DirClassLoader dc = new DirClassLoader("./resource/cloader");
		Class c = dc.loadClass("net.ion.bleujin.cloader.Hello");

		Object h = c.newInstance();
		MethodUtils.invokeMethod(h, "say", "bleujin");
	}

	public void testParent() throws Exception {
		ClassLoader dc = new DirClassLoader("./resource/cloader");
		while (true) {
			if (dc == null) {
				break;
			}
			Debug.debug(dc, dc.getClass());
			try {
				Debug.line(dc.loadClass("net.ion.bleujin.cloader.Hello"));
			} catch (ClassNotFoundException exp) {
			}
			dc = dc.getParent();
		}
	}
}
