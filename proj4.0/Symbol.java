//
//	class Symbol
//		written by Tim Budd
//

class Symbol {
	public Symbol (String n)  { name = n; }

	public final String name;

	public boolean equals (Object two) {
			// symbols are equal if equal in name
		if (two instanceof String) return name.equals(two);
		if (two instanceof Symbol) return two.equals(name);
		return false;
	}
}

class TypeSymbol extends Symbol {
	public TypeSymbol (String name, Type t) 
		{ super (name); type = t; } 

	public final Type type;
}


class ConstantSymbol extends Symbol {

	public ConstantSymbol (String name, Ast v) 
		{ super(name); value = v; }

	public final Ast value;
}

class OffsetSymbol extends Symbol {

	public OffsetSymbol (String name, Type t, int n) 
		{ super(name); type = t; location = n; }

	public final Type type;
	public final int location;
}


class GlobalSymbol extends Symbol {

	public GlobalSymbol (String name, Type t, String mn) 
		{ super(name); type = t; mangledName = mn; }

	public final Type type;
	private String mangledName;
}


