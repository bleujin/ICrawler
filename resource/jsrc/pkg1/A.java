package pkg1;
 
import pkg2.*;
 
public class A extends B {
	public void run(){
		new D().hello() ;
	}
}

class D {
	
	public void hello(){
		System.out.println("C Say : Hello");
	}
}