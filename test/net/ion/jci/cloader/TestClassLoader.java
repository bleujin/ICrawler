package net.ion.jci.cloader;

import java.io.File;
import java.io.FilenameFilter;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.codehaus.janino.Java;
import org.codehaus.janino.JavaSourceClassLoader;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.util.Traverser;

public class TestClassLoader extends TestCase {

	public void testLoadFromDir() throws Exception {
		ClassLoader cl = new JavaSourceClassLoader(this.getClass().getClassLoader(), // parentClassLoader
				new File[] { new File("./resource/jsrc") }, // optionalSourcePath
				"UTF-8"); // optionalCharacterEncoding

		// Load class A from "srcdir/pkg1/A.java", and also its superclass B from "srcdir/pkg2/B.java":
		Object o = cl.loadClass("pkg1.A").newInstance();

		// Class "B" implements "Runnable", so we can cast "o" to "Runnable".
		((Runnable) o).run(); // Prints "HELLO" to "System.out".

	}

	public void testTraverser() throws Exception {

		DeclarationCounter dc = new DeclarationCounter();
		File[] files = new File("./src/net/ion/jci/cloader").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		});

		for (File file : files) {
			// Parse each compilation unit.S
			Java.CompilationUnit cu = new Parser(new Scanner(file, "UTF-8")).parseCompilationUnit();
			dc.traverseCompilationUnit(cu); // Traverse it and count declarations.
			// UnparseVisitor.unparse(cu, new OutputStreamWriter(System.out));
		}

		Debug.println("Class declarations:     " + dc.classDeclarationCount);
		Debug.println("Interface declarations: " + dc.interfaceDeclarationCount);
		Debug.println("Fields:                 " + dc.fieldCount);
		Debug.println("Local variables:        " + dc.localVariableCount);
	}

}

class DeclarationCounter extends Traverser {

	int classDeclarationCount;
	int interfaceDeclarationCount;
	int fieldCount;
	int localVariableCount;

	// Count class declarations.
	@Override
	public void traverseClassDeclaration(Java.ClassDeclaration cd) {
		++this.classDeclarationCount;
		super.traverseClassDeclaration(cd);
	}

	// Count interface declarations.
	@Override
	public void traverseInterfaceDeclaration(Java.InterfaceDeclaration id) {
		++this.interfaceDeclarationCount;
		super.traverseInterfaceDeclaration(id);
	}

	// Count fields.
	@Override
	public void traverseFieldDeclaration(Java.FieldDeclaration fd) {
		this.fieldCount += fd.variableDeclarators.length;
		super.traverseFieldDeclaration(fd);
	}

	// Count local variables.
	@Override
	public void traverseLocalVariableDeclarationStatement(Java.LocalVariableDeclarationStatement lvds) {
		this.localVariableCount += lvds.variableDeclarators.length;
		super.traverseLocalVariableDeclarationStatement(lvds);
	}

}