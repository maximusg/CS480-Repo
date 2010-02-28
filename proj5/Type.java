//
//	internal type representations
//	written by Tim Budd
//

abstract class Type { 

	abstract public int size ( );
}

class PrimitiveType extends Type {
	private int sz;
		
		// static data fields for common types
	public static final Type VoidType = new PrimitiveType(0);
	public static final Type IntegerType = new PrimitiveType(4);
	public static final Type BooleanType = new PrimitiveType(2);
	public static final Type CharacterType = new PrimitiveType(1);
	public static final Type RealType = new PrimitiveType(8);

	public PrimitiveType (int s) { sz = s; }

	public int size ( ) { return sz; }

	public String toString() { 
		if (equals(IntegerType)) return "Integer";
		if (equals(BooleanType)) return "Boolean";
		if (equals(RealType)) return "Real";
		if (equals(CharacterType)) return "Character";
		return "Primitive type"; }
}

class PointerType extends PrimitiveType {
	protected final Type baseType;

	public PointerType (Type t) { super(4); baseType = t; }

	public boolean equals (Object t) {
		if (! (t instanceof PointerType))
			return false;
		PointerType pt = (PointerType) t;
		return baseType.equals(pt.baseType);
		}

	public String toString() { return "Pointer to " + baseType.toString(); }
}

class AddressType extends PrimitiveType {
	protected final Type baseType;

	public AddressType (Type t) { super(4); baseType = t; }

	public boolean equals (Object t) {
		if (! (t instanceof AddressType))
			return false;
		AddressType pt = (AddressType) t;
		return baseType.equals(pt.baseType);
		}

	public String toString() { return "Address to " + baseType.toString(); }
}

class StringType extends PointerType {

	public StringType (String n) { super(PrimitiveType.CharacterType); }

}

class ArrayType extends Type {
	public final int lowerBound;
	public final int upperBound;
	public final Type elementType;

	public ArrayType (int lb, int ub, Type bt)
		{ lowerBound = lb; upperBound = ub; elementType = bt; }

	public int size ( )
		{ return ((upperBound - lowerBound) + 1) * elementType.size(); }
		
	public String toString() {
		return "Array " + lowerBound + " to " + upperBound + 
			" of " + elementType;
		}

	public boolean equals (Object t) {
		if (! (t instanceof ArrayType))
			return false;
		ArrayType pt = (ArrayType) t;
		if ((pt.lowerBound != lowerBound) || (pt.upperBound != upperBound))
			return false;
		return elementType.equals(pt.elementType);
		}

}

class ClassType extends Type {
	public final SymbolTable symbolTable;

	public ClassType (SymbolTable ct) { symbolTable = ct; }

	public int size () { return symbolTable.size(); }

	public String toString() { return "class type"; }
}

class FunctionType extends Type {
	public Type returnType;

	public int size() { return 8; }

	public FunctionType (Type rt) { returnType = rt; }

	public String toString() { return "function type"; }
}

