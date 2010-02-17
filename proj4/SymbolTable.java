//
//	written by Tim Budd
//

interface SymbolTable {
		// methods to enter values into symbol table
	public void enterConstant (String name, Ast value);
	public void enterType (String name, Type type);
	public void enterIdentifier (String name, Type type);
	public void enterFunction (String name, FunctionType ft);

		// methods to search the symbol table
	public boolean nameDefined (String name);
	public Type lookupType (String name) throws ParseException;
	public Ast lookupName (Ast base, String name) throws ParseException;
	public int size ();
}

class GlobalSymbolTable implements SymbolTable {
	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterIdentifier (String name, Type type)
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private SymbolLink firstLink = null;
	private class SymbolLink {
		public Symbol sym;
		public SymbolLink link;
		public SymbolLink (Symbol s, SymbolLink l) 
			{ sym = s; link = l; }
	}
	private void enterSymbol (Symbol s) {
		firstLink = new SymbolLink(s, firstLink);
	}

	private Symbol findSymbol (String name) {
		for (SymbolLink l = firstLink; l != null; l = l.link)
			if (l.sym.name.equals(name))
				return l.sym;
		return null;
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		throw new ParseException(30);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof GlobalSymbol)) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if ((s != null) && (s instanceof ConstantSymbol)) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		throw new ParseException(42, name);
	}

	public int size () { return 0; }
}

class ClassSymbolTable implements SymbolTable {
	private SymbolTable surround = null;

	ClassSymbolTable (SymbolTable s) { surround = s; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	private int currentSize = 0;
	public void enterIdentifier (String name, Type type)
		{ 
			enterSymbol(new OffsetSymbol(name, new AddressType(type), currentSize));
			currentSize += type.size();
		}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private SymbolLink firstLink = null;
	private class SymbolLink {
		public Symbol sym;
		public SymbolLink link;
		public SymbolLink (Symbol s, SymbolLink l) 
			{ sym = s; link = l; }
	}
	private void enterSymbol (Symbol s) {
		firstLink = new SymbolLink(s, firstLink);
	}

	private Symbol findSymbol (String name) {
		for (SymbolLink l = firstLink; l != null; l = l.link)
			if (l.sym.name.equals(name))
				return l.sym;
		return null;
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		return surround.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof GlobalSymbol)) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if ((s != null) && (s instanceof OffsetSymbol)) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if ((s != null) && (s instanceof ConstantSymbol)) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return surround.lookupName(base, name);
	}

	public int size () { return currentSize; }
}

class FunctionSymbolTable implements SymbolTable {
	SymbolTable surrounding = null;

	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	private int paramOffset = 8;
	private int localOffset = 0;
	public void enterIdentifier (String name, Type type)
	{
		if (doingArguments) {
			enterSymbol(new OffsetSymbol(name, new AddressType(type), paramOffset));
			paramOffset += type.size();
		}
		else {
			localOffset += type.size();
			enterSymbol(new OffsetSymbol(name, new AddressType(type), - localOffset));
		}
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public boolean doingArguments = true;
	private SymbolLink firstLink = null;
	private class SymbolLink {
		public Symbol sym;
		public SymbolLink link;
		public SymbolLink (Symbol s, SymbolLink l) 
			{ sym = s; link = l; }
	}
	private void enterSymbol (Symbol s) {
		firstLink = new SymbolLink(s, firstLink);
	}

	private Symbol findSymbol (String name) {
		for (SymbolLink l = firstLink; l != null; l = l.link)
			if (l.sym.name.equals(name))
				return l.sym;
		return null;
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		return surrounding.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof GlobalSymbol)) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if ((s != null) && (s instanceof OffsetSymbol)) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if ((s != null) && (s instanceof ConstantSymbol)) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return surrounding.lookupName(base, name);
	}

	public int size () { return localOffset; }
}
