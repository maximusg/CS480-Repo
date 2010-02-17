//
//	code generation classes
//	written by Tim Budd, spring 2000
//

class CodeGen {
	
	static void genProlog (String name, int size) {
		System.out.println("Begin function " + name);
		System.out.println("local space " + size);
		}

	static void genEpilog (String name) {
		System.out.println("End function " + name);
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
}

class Label {
	static int number = 0;
	public int n;

	Label () { n = ++number; }

	public String toString() { return "Label " + n; }

	void genCode () { System.out.println(".L"+n+":"); }

	void genBranch () { System.out.println("branch to L" + n); }

	void genBranch (String cond) { 
		System.out.println("\t" + cond + "\t.L" + n); }
}

