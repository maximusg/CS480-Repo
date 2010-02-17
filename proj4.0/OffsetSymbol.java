class OffsetSymbol extends Symbol {

	public OffsetSymbol (String name, Type t, int n) 
		{ super(name); type = t; location = n; }

	public final Type type;
	public final int location;
}

