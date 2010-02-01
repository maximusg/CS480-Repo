//
//	written (and rewritten) by Tim Budd
//

interface SymbolTable {
		// methods to enter values into symbol table
	public void enterConstant (String name, Ast value);
	public void enterType (String name, Type type);
	public void enterVariable (String name, Type type);
	public void enterFunction (String name, FunctionType ft);
	public int size();

		// methods to search the symbol table
	public boolean nameDefined (String name);
	public Type lookupType (String name) throws ParseException;
	public Ast lookupName (Ast base, String name) throws ParseException;
}

class GlobalSymbolTable implements SymbolTable {
	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private void enterSymbol (Symbol s) {
		// this if for you to figure out.
		// how should a symbol be stored?
		// ...
	}

	private Symbol findSymbol (String name) {
		// this is also for you to figure out.
		// read a symbol.  If not found, return null
		// ...
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
		if (s == null)
			throw new ParseException(41, name);
		// now have a valid symbol
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}
}

class FunctionSymbolTable implements SymbolTable {
	SymbolTable surrounding = null;

	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
	{
		// this is for you to figure out.
		// I'll leave a stub, which you should
		// replace with the real thing
		enterSymbol(new OffsetSymbol(name, new AddressType(type), 27));
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public boolean doingArguments = true;

	private void enterSymbol (Symbol s) {
		// you can just copy from the first one
	}

	private Symbol findSymbol (String name) {
		// 
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
		// note how we check the surrounding scopes
		return surrounding.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			return surrounding.lookupName(base, name);
		// we have a symbol here
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof OffsetSymbol) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}
}

class ClassSymbolTable implements SymbolTable {
	private SymbolTable surround = null;

	ClassSymbolTable (SymbolTable s) { surround = s; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
		{ 
			// again, you need to do something different here.
			enterSymbol(new OffsetSymbol(name, new AddressType(type), 27));
		}

	public void enterFunction (String name, FunctionType ft) 
		// this should really be different as well,
		// but we will leave alone for now
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private void enterSymbol (Symbol s) {
		// ...
	}

	private Symbol findSymbol (String name) {
		// ...
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
		if (s == null)
			return surround.lookupName(base, name);
		// else we have a symbol here
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof OffsetSymbol) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}
}
