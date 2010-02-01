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
