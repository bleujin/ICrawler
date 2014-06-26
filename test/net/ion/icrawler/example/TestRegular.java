package net.ion.icrawler.example;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestRegular extends TestCase {

	public void testDate() throws Exception {
		String d = "2012/07/02 07:20" ;
		Debug.line(d.matches("\\d+\\/\\d+\\/\\d+\\s+\\d+:\\d+")) ;
	}
}
