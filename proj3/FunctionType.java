class FunctionType extends Type {
	public Type returnType;

	public int size() { return 8; }

	public FunctionType (Type rt) { returnType = rt; }

	public String toString() { return "function type"; }
}

