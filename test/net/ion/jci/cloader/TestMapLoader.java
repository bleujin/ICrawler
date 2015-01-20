package net.ion.jci.cloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import junit.framework.TestCase;

public class TestMapLoader extends TestCase{

	
	public void testNotFound() throws Exception {
		Class<?> clz = getClass().getClassLoader().loadClass("net.ion.bleujin.cloader.Person");
		Debug.line(clz.getDeclaredConstructors());
	}

	public void testFromFile() throws Exception {
		MapLoader ml = new MapLoader() ;
		Class<?> clz = ml.loadClass("net.ion.bleujin.cloader.Person");
		
		Debug.line(clz.getDeclaredConstructors()) ;
	}
	
	

}


class MapLoader extends ClassLoader {

	public MapLoader() throws IOException {
		init() ;
	}
	
	private void init() throws FileNotFoundException, IOException {
		byte[] bytes = IOUtil.toByteArrayWithClose(new FileInputStream("./resource/cloader/Person.class")) ;
		defineBytecode("net.ion.bleujin.cloader.Person", bytes) ;
	}


	private Class defineBytecode(String className, byte[] ba) {
		return this.defineClass(className, ba, 0, ba.length, null);
	}

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
    	System.out.println(name);
        return super.loadClass(name);
    }
	

}