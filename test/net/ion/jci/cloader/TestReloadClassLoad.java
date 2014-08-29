package net.ion.jci.cloader;

import java.io.File;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.jci.cloader.OuterClassLoader;
import net.ion.jci.cloader.ReloadSourceClassLoader;
import net.ion.jci.monitor.AbstractListener;
import net.ion.jci.monitor.FileAlterationMonitor;

import org.apache.commons.io.monitor.FileAlterationObserver;

public class TestReloadClassLoad extends TestCase {

	public void testReloadSourceLoader() throws Exception {
		
		
		final File srcDir = new File("./resource/jsrc") ;
		
		ReloadSourceClassLoader inner = new ReloadSourceClassLoader(getClass().getClassLoader(), new File[] {srcDir }, "UTF-8");
		
		final OuterClassLoader classloader = new OuterClassLoader(inner);

		FileAlterationObserver fo = new FileAlterationObserver(srcDir) ;
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				Debug.line(file + " changed !");
				classloader.change(new ReloadSourceClassLoader(getClass().getClassLoader(), new File[] { srcDir }, "UTF-8"));
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(3000, Executors.newScheduledThreadPool(1), fo);
		fam.start();

		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						Object o = classloader.loadClass("pkg1.A").newInstance();
						((Runnable) o).run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		

		new InfinityThread().startNJoin();

	}
	
}
