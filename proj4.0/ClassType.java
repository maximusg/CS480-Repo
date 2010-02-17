class ClassType extends Type {
	public final SymbolTable symbolTable;

	public ClassType (SymbolTable ct) { symbolTable = ct; }

	public int size () { return symbolTable.size(); }

	public String toString() { return "class type"; }
}
