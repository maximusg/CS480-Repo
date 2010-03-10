//
//	abstract syntax tree
//

import java.util.Vector;

abstract class Ast {
	public Ast(Type t) { type = t; }

	public Type type;

	abstract public void genCode ();

	public Ast optimize () { return this; }

	public void branchIfTrue (Label lab) throws ParseException {
		genCode();
		System.out.println("Branch if True " + lab);
	}

	public void branchIfFalse (Label lab) throws ParseException { 
		genCode();
		System.out.println("Branch if False " + lab);
	}

	public boolean isInteger() { return false; }

	public int cValue() { return 0; }

	public BinaryNode isSum() { return null; }
}

class GlobalNode extends Ast {
	public GlobalNode (Type t, String n) { super(t); name = n;}

	public String name;

	public String toString() { return "global node " + name; }

	public void genCode() {
		System.out.println("Global " + name + " " + type);
		}
}

class IntegerNode extends Ast {
	public int val;

	public IntegerNode (int v) 
		{ super(PrimitiveType.IntegerType); val = v; }
	public IntegerNode (Integer v) 
		{ super(PrimitiveType.IntegerType); val = v.intValue(); }

	public String toString() { return "Integer " + val; }

	public void genCode() {
		System.out.println("Integer " + val);
		}

	public boolean isInteger() { return true; }

	public int cValue() { return val; }
}

class RealNode extends Ast {
	private double val;

	public RealNode (double v) 
		{ super(PrimitiveType.RealType); val = v; }
	public RealNode (Double v) 
		{ super(PrimitiveType.RealType); val = v.doubleValue(); }

	public String toString() { return "real " + val; }

	public void genCode() {
		System.out.println("Real " + val);
		}
}

class StringNode extends Ast {
	private String val;

	public StringNode (String v) 
		{ super(new StringType(v)); val = v; }

	public String toString() { return "string " + val; }

	public void genCode() {
		System.out.println("String " + val); 
		}
}

class FramePointer extends Ast {
	public FramePointer () { super(PrimitiveType.VoidType); }

	public void genCode () {
		System.out.println("frame pointer");
		}

	public String toString() { return "frame pointer"; }
}

class UnaryNode extends Ast {
	static final int dereference = 1;
	static final int convertToReal = 2;
	static final int notOp = 3;
	static final int negation = 4;
	static final int newOp = 5;


	public UnaryNode (int nt, Type t, Ast b) { 
		super(t); 
		nodeType = nt;
		child = b;
	}

	public int nodeType;
	public Ast child;

	public String toString() { return "Unary node " + nodeType +
		"(" + child + ")" + type; }

	public Ast optimize() {
		Ast newChild = child.optimize();
		if ((nodeType == negation) && newChild.isInteger())
			return new IntegerNode(- newChild.cValue());
		return new UnaryNode(nodeType, type, child.optimize());
		}

	public void genCode () {
		child.genCode();
		switch(nodeType) {
			case dereference:
				System.out.println("dereference " + type); break;
			case convertToReal:
				System.out.println("convert to real" + type); break;
			case notOp:
				System.out.println("not op " + type); break;
			case negation:
				System.out.println("numeric negation " + type); break;
			case newOp:
				System.out.println("new memory " + type); break;
		}
	}
}

class BinaryNode extends Ast {
	static final int plus = 1;
	static final int minus = 2;
	static final int times = 3;
	static final int divide = 4;
	static final int and = 5;
	static final int or = 6;
	static final int less = 7;
	static final int lessEqual = 8;
	static final int equal = 9;
	static final int notEqual = 10;
	static final int greater = 11;
	static final int greaterEqual = 12;
	static final int leftShift = 13;
	static final int remainder = 14;

	public BinaryNode isSum() { 
		if (NodeType == plus) return this;
		return null; 
	}

	public BinaryNode (int nt, Type t, Ast l, Ast r) { 
		super(t); 
		NodeType = nt;
		LeftChild = l;
		RightChild = r;
		}

	public String toString() { return "Binary Node " + NodeType +
		"(" + LeftChild + "," + RightChild + ")" + type; }

	public Ast optimize() {
//System.out.println("Optimizing " + toString());
		Ast newLeft = LeftChild.optimize();
//System.out.println("Left child is " + newLeft);
		Ast newRight = RightChild.optimize();
//System.out.println("Right child is " + newRight);
		switch (NodeType) {
			case plus:
				if (newRight.isInteger()) {
					int c = newRight.cValue();
					if (c == 0) {
						newLeft.type = type;
						return newLeft;
						}
					if (newLeft.isInteger())
						return new IntegerNode
						(c + newLeft.cValue());
					BinaryNode leftSum = newLeft.isSum();
					if ((leftSum != null) &&
						leftSum.RightChild.isInteger())
						return new BinaryNode(plus,
						type,
						leftSum.LeftChild,
						new IntegerNode(c +
						leftSum.RightChild.cValue())).optimize();
					}
				if (newLeft.isInteger())
					return new BinaryNode(plus, type,
						newRight, newLeft).optimize();
				BinaryNode LeftSum = newLeft.isSum();
				if ((LeftSum != null) && 
					LeftSum.RightChild.isInteger())
					return new BinaryNode(plus, type,
					new BinaryNode(plus, type,
					LeftSum.LeftChild, newRight),
					LeftSum.RightChild).optimize();
				BinaryNode rightSum = newRight.isSum();
				if ((rightSum != null) &&
					rightSum.RightChild.isInteger())
					return new BinaryNode(plus, type,
					new BinaryNode(plus, type,
					newLeft, rightSum.LeftChild),
					rightSum.RightChild).optimize();
					
			break;

			case minus:
				if (newRight.isInteger())
					return new BinaryNode(plus, type,
						newLeft, 
						new IntegerNode(- newRight.cValue())).optimize();
			break;

			case times:
				if (newRight.isInteger()) {
					int c = newRight.cValue();
					if (c == 0) {
						newRight.type = type;
						return newRight;
						}
					if (c == 1) {
						newLeft.type = type;
						return newLeft;
						}
					if (newLeft.isInteger())
						return new IntegerNode(
							c * newLeft.cValue());
					BinaryNode leftSum = newLeft.isSum();
					if ((leftSum != null) &&
						leftSum.RightChild.isInteger())
						return new BinaryNode(plus,
						type,
						new BinaryNode(times,
						leftSum.type,
						leftSum.LeftChild,
						newRight),
						new IntegerNode(c *
						leftSum.RightChild.cValue())).optimize();

				}
				break;
		}
		return new BinaryNode(NodeType, type, newLeft, newRight);
	}

	public void genCode () {
		//LeftChild.genCode();
		//RightChild.genCode();
		switch (NodeType) {
			case plus:
				if (this.type == PrimitiveType.RealType){
					RightChild.genCode();
					LeftChild.genCode();
					CodeGen.gen("flds",	"0(%esp)");
					CodeGen.gen("addl",	"$4", "%esp");
					CodeGen.gen("fadds", "0(%esp)");
					CodeGen.gen("fstps", "0(%esp)");
				} else if (this.type == PrimitiveType.IntegerType){
					LeftChild.genCode();
					if (RightChild.isInteger()){
						CodeGen.gen("addl",	"$n", "0(%esp)");
					} else {
						RightChild.genCode();
						CodeGen.gen("popl",	"%eax");
						CodeGen.gen("addl",	"%eax", "0(%esp)");
					}
				}
				break;
			case minus: 
				if (this.type == PrimitiveType.RealType){
					RightChild.genCode();
					LeftChild.genCode();
					CodeGen.gen("flds",	"0(%esp)");
					CodeGen.gen("addl",	"$4", "%esp");
					CodeGen.gen("fsubs", "0(%esp)");
					CodeGen.gen("fstps", "0(%esp)");
				} else {
					LeftChild.genCode();
					RightChild.genCode();
					CodeGen.gen("popl",	"%eax");
					CodeGen.gen("subl",	"%eax", "0(%esp)");
				}
				break;
			case leftShift: 
				System.out.println("do left shift " + type); break;
			case times: 
				if (this.type == PrimitiveType.RealType){
					RightChild.genCode();
					LeftChild.genCode();
					CodeGen.gen("flds",	"0(%esp)");
					CodeGen.gen("addl",	"$4", "%esp");
					CodeGen.gen("fmuls", "0(%esp)");
					CodeGen.gen("fstps", "0(%esp)");
				} else {
					LeftChild.genCode();
					RightChild.genCode();
					CodeGen.gen("popl",	"%eax");
					CodeGen.gen("imull", "0(%esp)");
					CodeGen.gen("movl",	"%eax", "0(%esp)");
				}
				break;
			case divide: 
				System.out.println("do division " + type); break;
			case remainder:
				System.out.println("do remainder " + type); break;
			case and: 
				System.out.println("do and " + type); break;
			case or: 
				System.out.println("do or " + type); break;
			case less: 
				System.out.println("compare less " + type); break;
			case lessEqual: 
				System.out.println("compare less or equal" + type); break;
			case equal: 
				System.out.println("compare equal " + type); break;
			case notEqual: 
				System.out.println("compare notEqual " + type); break;
			case greater: 
				System.out.println("compare greater " + type); break;
			case greaterEqual: 
				System.out.println("compare greaterEqual " + type); break;
			}
		}

	public int NodeType;
	public Ast LeftChild;
	public Ast RightChild;
}

class FunctionCallNode extends Ast {
	private Ast fun;
	protected Vector args;

	public FunctionCallNode (Ast f, Vector a) {
		super (((FunctionType) f.type).returnType);
		fun = f;
		args = a;
		}

	public String toString() { return "Function Call Node"; }

	public Ast optimize() {
		Vector newArgs = new Vector();
		for (int i = 0; i < args.size(); i++) {
			Ast arg = (Ast) args.elementAt(i);
			newArgs.addElement(arg.optimize());
			}
		return new FunctionCallNode(fun, newArgs);
	}

	public void genCode () {
		int i = args.size();
		while (--i >= 0) {
			Ast arg = (Ast) args.elementAt(i);
			arg.genCode();
			System.out.println("push argument " + arg.type);
			}

		fun.genCode();
		System.out.println("function call " + type);
	}
}
