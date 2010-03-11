import java.util.Vector;

class CodeGen {
	
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

	static void genProlog (String name, int size) {
		gen(".globl", name);
		gen(".type", name, "@function");
		System.out.println(name + ":");
		gen("pushl", "%ebp");
		gen("movl",	"%esp", "%ebp");
		if (size != 0){
			String sizeString = String.valueOf(size);
			gen("subl", "$" + sizeString, "%esp");
		}
		
		endLabel = new Label();
		constantTable = new Vector();
		stringLabel = new Vector();
		}

	static void addConstant(Label l, Object s) {
		stringLabel.addElement(l);
		constantTable.addElement(s);
		}

	static void genEpilog (String name) {
		endLabel.genCode();
		gen("leave");
		gen("ret");
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
		String sizeString = String.valueOf(size);
		gen(".comm", name, sizeString);
		
		System.out.println("replace me with your code");
		}

	static void genAssign (Ast left, Ast right) {
		right.genCode();
		if(left instanceof BinaryNode){//Check that the whole thing works
			if(((BinaryNode)left).LeftChild instanceof FramePointer){
				if(((BinaryNode)left).RightChild.isInteger()){
					if(((BinaryNode)left).NodeType == BinaryNode.plus){
						if (right.type == PrimitiveType.RealType){
							gen("flds","0(%esp)");
							gen("addl",	"$4","%esp");
							gen("fstps",((BinaryNode)left).RightChild.cValue()+"(%ebp)");
						}
						else{
							gen("popl",	((BinaryNode)left).RightChild.cValue()+"(%ebp)");
						}
					}
				}
			}
		}
		if(left instanceof GlobalNode ){
			 gen("popl", "%eax");
			 gen("movl", "%eax", ((GlobalNode)(left)).name);
		}
		if(!(left instanceof GlobalNode) && !(left instanceof BinaryNode)){
				left.genCode();
				right.genCode();
				if(left.type == PrimitiveType.RealType){
					gen("flds","0(%esp)");
					gen("addl","$4","%esp");
					gen("popl","%ecx");
					gen("fstps","0(%ecx)");
				}
				else{
					gen("popl",	"%eax");
					gen("popl"	,"%ecx");
					gen("movl",	"%eax","0(%ecx)");
				}
		}
	}

	static void genReturn (Ast e) {
		// put your code here
		if (e != null) {
			e.genCode();
			if(e.type == PrimitiveType.RealType){
				gen("fld","0(%esp)");
				gen("addl","$4,%esp");
			}
			else{
				gen("popl",	"%eax");
			}	
			
		}
		
		endLabel.genBranch();
	}
}

class Label {
	static int number = 0;
	public int n;

	Label () { n = ++number; }

	public String toString () { return ".L" + n; }

	public void genCode () { System.out.println(toString()+":"); }

	public void genBranch () { genBranch("jmp"); }

	public void genBranch (String cond) { 
		CodeGen.gen(cond, toString());
	}
}

