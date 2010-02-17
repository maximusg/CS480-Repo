class ConstantSymbol extends Symbol {

	public ConstantSymbol (String name, Ast v) 
		{ super(name); value = v; }

	public final Ast value;
}

