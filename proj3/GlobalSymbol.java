class GlobalSymbol extends Symbol {

	public GlobalSymbol (String name, Type t, String mn) 
		{ super(name); type = t; mangledName = mn; }

	public final Type type;
	private String mangledName;
}

