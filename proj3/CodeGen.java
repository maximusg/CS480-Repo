//
//	code generation classes
//	written by Tim Budd, spring 2000
//

import java.util.Vector;

class CodeGen {
	
	static void genProlog (String name, int size) {
		System.out.println("Begin function " + name);
		System.out.println("local space " + size);
			// create space for constant pool
		endLabel = new Label();
		constantTable = new Vector();
		stringLabel = new Vector();
		}

	static void addConstant(Label l, Object s) {
		stringLabel.addElement(l);
		constantTable.addElement(s);
		}

	static void genEpilog (String name) {
		System.out.println("End function " + name);
			// now dump out constant pool
		for (int i = 0; i < constantTable.size(); i++) {
			gen(".align","4");
			Label l = (Label) stringLabel.elementAt(i);
			l.genCode();
			Object v = constantTable.elementAt(i);
			if (v instanceof String)
				gen(".string", "\"" + v + "\"");
			else if (v instanceof Double)
				gen(".float", "" + v);
			}
		}

	static void genGlobal (String name, int size) {
		System.out.println("Global " + name + " size " + size);
		}

	static void genAssign (Ast left, Ast right) {
		left.genCode();
		right.genCode();
		System.out.println("do assignment");
		}

	static void genReturn (Ast e) {
		if (e != null)
			e.genCode();
		System.out.println("return from function");
		}

	static void gen (String op) {
		System.out.println("\t" + op);
		}

	static void gen (String op, String a) {
		System.out.println("\t" + op + "\t" + a);
		}

	static void gen (String op, String a, String b) {
		System.out.println("\t" + op + "\t" + a + "," + b);
		}

	static private Label endLabel;
	static private Vector stringLabel;
	static private Vector constantTable;
}

class Label {
	static int number = 0;
	public int n;

	Label () { n = ++number; }

	public String toString() { return ".L" + n; }

	void genCode () { System.out.println(toString()+":"); }

	void genBranch () { genBranch("branch to"); }

	void genBranch (String cond) { 
		CodeGen.gen(cond, toString());
	}
}

