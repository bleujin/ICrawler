package net.ion.jci.cloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;

import org.apache.commons.lang.reflect.MethodUtils;

public class TestChangeLoader extends TestCase {

	public void testAdd() throws Exception {
		ChangeClassLoader cl = new ChangeClassLoader();
		Class cz = cl.loadClass("net.ion.bleujin.cloader.Dept");

		MethodUtils.invokeStaticMethod(cz, "main", (Object)new String[0]);
	}

}


class ChangeClassLoader extends ClassLoader {
	
	public ChangeClassLoader() throws IOException {
		super();
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return findClass(name) ;
		} catch(ClassNotFoundException ex){
			return super.loadClass(name) ;
		}
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {

		AccessControlContext acc = AccessController.getContext();
		try {
			return (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws ClassNotFoundException {

					InputStream fi = null;
					try {
						String path = name.replace('.', '/');
						fi = new FileInputStream("./bin/" + path + ".class");
						if ("net.ion.bleujin.cloader.Person".equals(name)){
							fi = new FileInputStream("./resource/cloader/Person.class");
						}
						
						byte[] classBytes = IOUtil.toByteArray(fi) ;
						return defineClass(name, classBytes, 0, classBytes.length);
					} catch (Exception e) {
						throw new ClassNotFoundException(name);
					} finally {
						IOUtil.close(fi);
					}
				}
			}, acc);
		} catch (java.security.PrivilegedActionException pae) {
			return super.findClass(name);
		}
	}
}
