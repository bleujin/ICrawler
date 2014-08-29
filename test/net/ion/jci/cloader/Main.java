package net.ion.jci.cloader;

import net.ion.framework.util.Debug;

public class Main implements Runnable{

	public void run(){
		Debug.line(new HI().hello(), "dd");  
	}
}


class HI {
	
	public String hello(){
		return "helo" ; 
	}
}