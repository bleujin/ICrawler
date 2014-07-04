package net.ion.jci.cloader;

import java.lang.reflect.Method;

import net.ion.framework.util.Debug;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IClassBodyEvaluator;
import org.codehaus.janino.ExpressionEvaluator;

import junit.framework.TestCase;

public class TestExpression extends TestCase {

	public void testFirst() throws Exception {
		ExpressionEvaluator ee = new ExpressionEvaluator("c > d ? c - d  : d - c", // expression
				int.class, // expressionType
				new String[] { "c", "d" }, // parameterNames
				new Class[] { int.class, int.class } // parameterTypes
		);

		// Evaluate it with varying parameter values; very fast.
		Integer res = (Integer) ee.evaluate(new Object[] { new Integer(10), new Integer(11) }); // parameterValues
		System.out.println("res = " + res);
	}

	public void testCal() throws Exception {
		ExpressionEvaluator ee = new ExpressionEvaluator("Math.pow(0.75, 40)", // expression
				double.class, new String[0], new Class[0]);

		Double res = (Double) ee.evaluate(new Object[0]); // parameterValues
		System.out.println("res = " + res);
	}

	public void testLogic() throws Exception {
		String classBody = "import java.util.*; \n" 
				+" // Field declaration: \n" 
				+ "private static final String hello = \"World\";\n" 
				+ "// Method declaration:\n" 
				+ "public static void main(String[] args) {\n" 
				+ "		System.out.println(hello + args.length);\n" 
				+ "}\n";

		IClassBodyEvaluator cbe = CompilerFactoryFactory.getDefaultCompilerFactory().newClassBodyEvaluator();
		cbe.cook(classBody);
		Class c = cbe.getClazz();

		Method m = c.getMethod("main", String[].class);
		m.invoke(null, (Object)(new String[] { "red" ,"bleu", "white" }));
	}

	
}
