//
//	abstract syntax tree
//

import java.util.Vector;

abstract class Ast {
	
	public Ast optimize() { return this; }
	
	public Ast(Type t) { type = t; }

	public Type type;

	abstract public void genCode ();

	public void branchIfTrue (Label lab) throws ParseException {
		optimize().genCode();
		System.out.println("Branch if True " + lab);
	}

	public void branchIfFalse (Label lab) throws ParseException { 
		optimize().genCode();
		System.out.println("Branch if False " + lab);
	}
	
	protected boolean isIntegerConstant() {
		return (this instanceof IntegerNode);
	}
	
	protected int getConstIntVal() {
		return ((IntegerNode)this).val;
	}
	
	protected BinaryNode isAddition() {
		if((((BinaryNode)this).NodeType == (BinaryNode.plus))){
			return new BinaryNode(BinaryNode.plus, this.type, this, this);
		}
		return null;
	}
	
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

	public Ast optimize() {
		child = child.optimize();
		if (nodeType == negation && child.isIntegerConstant()){
			return new IntegerNode(child.getConstIntVal()*-1);
		} else {
			child = child.optimize();
		}
		
		return new UnaryNode(this.nodeType, this.type, this.child);
	}
	
	public UnaryNode (int nt, Type t, Ast b) { 
		super(t); 
		nodeType = nt;
		child = b;
	}

	public int nodeType;
	public Ast child;

	public String toString() { return "Unary node " + nodeType +
		"(" + child + ")" + type; }

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

	public BinaryNode (int nt, Type t, Ast l, Ast r) { 
		super(t); 
		NodeType = nt;
		LeftChild = l;
		RightChild = r;
		}

	public Ast optimize() { 
		Ast left = LeftChild.optimize();
		Ast right = RightChild.optimize();
		
		//t + 0
		if((this.NodeType == plus) && 
				(right.isIntegerConstant()) && 
				(right.getConstIntVal() == 0) ){
			left.type = this.type;
			return left;
		//c + c
		}else if((this.NodeType == plus) && 
				(right.isIntegerConstant()) && 
				(left.isIntegerConstant())){
			return new IntegerNode(new Integer(left.getConstIntVal() + right.getConstIntVal()));
		//c + t
		}else if((this.NodeType == plus) && 
				(left.isIntegerConstant()) && 
				!(right.isIntegerConstant())){
			return new BinaryNode(NodeType,type,right,left);
		//(t+c) + c
		}else if((this.NodeType == plus) && 
				(left.isIntegerConstant()) && 
				(left instanceof BinaryNode)){
			BinaryNode l = (BinaryNode)left;
			if((l.NodeType == plus) && 
					(!(l.LeftChild.isIntegerConstant())) && 
					(l.RightChild.isIntegerConstant())){
				return new BinaryNode(NodeType,type,
						l.LeftChild,
						new IntegerNode(new Integer(right.getConstIntVal() + l.RightChild.getConstIntVal())));
			}
		//(t+c) + t2
		}else if((this.NodeType == plus) && 
				(!right.isIntegerConstant()) && 
				(left instanceof BinaryNode)){
			BinaryNode l = (BinaryNode)left;
			if ((l.NodeType == plus) && 
					(l.RightChild.isIntegerConstant()) && 
					(!l.LeftChild.isIntegerConstant())){
				return (new BinaryNode(NodeType,type,
						new BinaryNode(NodeType,type,l.LeftChild,right),
						l.RightChild)).optimize();
			}
		//t+(t2+c)
		}else if((this.NodeType == plus) && 
				(!left.isIntegerConstant()) && 
				(right instanceof BinaryNode)){
			BinaryNode r = (BinaryNode)right;
			if ((r.NodeType == plus) && 
					(!(r.LeftChild.isIntegerConstant())) && 
					(r.RightChild.isIntegerConstant())){
				return (new BinaryNode(NodeType,type,
						new BinaryNode(NodeType,type,left,r.LeftChild),
						r.RightChild)).optimize();
			}
		//t-c
		}else if((this.NodeType == minus) && 
				(!left.isIntegerConstant()) && 
				right.isIntegerConstant()){
			UnaryNode nr = (new UnaryNode(UnaryNode.negation,right.type,right));
			return (new BinaryNode(plus,type,left,nr)).optimize();
		//t*0
		}else if((this.NodeType == times) && 
				(right.isIntegerConstant()) && 
				(right.getConstIntVal() == 0) && 
				(!(left.isIntegerConstant()))){
			return new IntegerNode(new Integer(0));
		//t*1
		}else if((this.NodeType == times) && 
				(right.isIntegerConstant()) && 
				(right.getConstIntVal() == 1) && 
				(!left.isIntegerConstant())){
			return left;
		//c*c
		}else if((this.NodeType == times) && 
				(left.isIntegerConstant()) && 
				(right.isIntegerConstant())){
			return new IntegerNode(new Integer(left.getConstIntVal()) * right.getConstIntVal());
		//(t+c1)*c2
		}else if((this.NodeType == times) && 
				(right.isIntegerConstant()) && 
				(left instanceof BinaryNode)){
			BinaryNode l = (BinaryNode)left;
			if((!l.LeftChild.isIntegerConstant()) && 
					(l.RightChild.isIntegerConstant()) && 
					(l.NodeType == plus)){
				return (new BinaryNode(plus,type,
						new BinaryNode(times,type,l.LeftChild, right),
						new BinaryNode(times,type,l.RightChild,right))).optimize();
			}
		}

		return new BinaryNode(NodeType,type,left,right); 
	}
	
	public String toString() { return "Binary Node " + NodeType +
		"(" + LeftChild + "," + RightChild + ")" + type; }

	public void genCode () {
		LeftChild.genCode();
		RightChild.genCode();
		switch (NodeType) {
			case plus: 
				System.out.println("do addition " + type); break;
			case minus: 
				System.out.println("do subtraction " + type); break;
			case leftShift: 
				System.out.println("do left shift " + type); break;
			case times: 
				System.out.println("do multiplication " + type); break;
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
	
	public Ast optimize() {
		Vector rgs = new Vector();
		for(int i = 0; i < args.capacity(); i++){
			rgs.add(((Ast)args.elementAt(i)).optimize());
		}
		args = rgs;
		return this;
	}

	public FunctionCallNode (Ast f, Vector a) {
		super (((FunctionType) f.type).returnType);
		fun = f;
		args = a;
		}

	public String toString() { return "Function Call Node"; }

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
