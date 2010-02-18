//
//	written (and rewritten) by Tim Budd
//

import java.util.*;

interface SymbolTable {
		// methods to enter values into symbol table
	public void enterConstant (String name, Ast value) throws ParseException;
	public void enterType (String name, Type type) throws ParseException;
	public void enterVariable (String name, Type type) throws ParseException;
	public void enterFunction (String name, FunctionType ft) throws ParseException;
	public int size();

		// methods to search the symbol table
	public boolean nameDefined (String name);
	public Type lookupType (String name) throws ParseException;
	public Ast lookupName (Ast base, String name) throws ParseException;
}

class GlobalSymbolTable implements SymbolTable {
	private Map<String, Symbol> sym = new TreeMap<String, Symbol>();
	
	public void enterConstant (String name, Ast value) throws ParseException
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type ) throws ParseException
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type) throws ParseException
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction (String name, FunctionType ft) throws ParseException 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public void enterSymbol (Symbol s) throws ParseException
	{
		if (sym.containsValue(s))
			throw new ParseException(35, s.name);
		sym.put(s.name, s);
	}

	public Symbol findSymbol (String name) {
		return (sym.containsKey(name) ? sym.get(name) : null);
	}

	public boolean nameDefined (String name) {
		return findSymbol(name) != null;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null){
			throw new ParseException(42, name);
		} else if (s instanceof TypeSymbol) {
			return ((TypeSymbol) s).type;
		} else {
			throw new ParseException(30, name);
		}
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null){
			throw new ParseException(42, name);
		} else if (s instanceof GlobalSymbol) {
			return new GlobalNode(((GlobalSymbol) s).type, name);
		} else if (s instanceof ConstantSymbol) {
			return ((ConstantSymbol) s).value;
		} else {
			return null; // should never happen
		}
	}

	@Override
	public int size() {
		// Returns 0 because it is the global symbol table.
		return 0;
	}
}

class FunctionSymbolTable implements SymbolTable {
	private Map<String, Symbol> sym = new TreeMap<String, Symbol>();
	private int sz = 0;
	private int argSz = 0;
	
	private GlobalSymbolTable surrounding = null;

	FunctionSymbolTable (GlobalSymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value)  throws ParseException
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type)  throws ParseException
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type) throws ParseException
		{ 
			if (doingArguments){
				enterSymbol(new OffsetSymbol(name, new AddressType(type), (8 + argSz)));
				argSz += type.size();
			} else {
				enterSymbol(new OffsetSymbol(name, new AddressType(type), 0 - sz - type.size()));
				sz += type.size();
			}
		}

	public void enterFunction (String name, FunctionType ft)  throws ParseException
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public boolean doingArguments = true;

	private void enterSymbol (Symbol s) throws ParseException {
		if (sym.containsValue(s))
			throw new ParseException(35, s.name);
		sym.put(s.name, s);
	}

	private Symbol findSymbol (String name) {
		return (sym.containsKey(name) ? sym.get(name) : null);
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

	@Override
	public int size() {
		return sz;
	}
}

class ClassSymbolTable implements SymbolTable {
	private Map<String, Symbol> sym = new TreeMap<String, Symbol>();
	private int sz = 0;
	
	private GlobalSymbolTable surround = null;

	ClassSymbolTable (GlobalSymbolTable s) { surround = s; }

	public void enterConstant (String name, Ast value) throws ParseException 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) throws ParseException 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type) throws ParseException
		{ enterSymbol(new OffsetSymbol(name, new AddressType(type), sz)); sz += type.size();  }

	public void enterFunction (String name, FunctionType ft) throws ParseException 
		{ throw new ParseException(0); }

	private void enterSymbol (Symbol s) throws ParseException {
		if (sym.containsValue(s))
			throw new ParseException(35, s.name);
		sym.put(s.name, s);
	}

	private Symbol findSymbol (String name) {
		return (sym.containsKey(name) ? sym.get(name) : null);
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

	@Override
	public int size() {
		return sz;
	}
}
