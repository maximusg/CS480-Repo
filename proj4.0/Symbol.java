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

