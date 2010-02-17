class TypeSymbol extends Symbol {
	public TypeSymbol (String name, Type t) 
		{ super (name); type = t; } 

	public final Type type;
}

