//
//	class ParseExeption
//		errors reported by the parser
//

class ParseException extends Exception {
	private static String messages [ ] = {
		"unexpected IO error",			// 0
		"unterminated comment",			// 1
		"unterminated string literal",		// 2
		"expecting end of file", 		// 3
		"expecting keyword begin",		// 4
		"expecting keyword class",		// 5
		"expecting keyword const",		// 6
		"expecting keyword do",			// 7
		"expecting keyword end",		// 8
		"expecting keyword extends",		// 9
		"expecting keyword function",		// 10
		"expecting keyword if",			// 11
		"expecting keyword return",		// 12
		"expecting keyword then",		// 13
		"expecting keyword type",		// 14
		"expecting keyword var",		// 15
		"expecting keyword while",		// 16
		"expecting period",			// 17
		"expecting semicolon",			// 18
		"expecting colon",			// 19
		"expecting assignment arrow",		// 20
		"expecting left parenthesis",		// 21
		"expecting right parenthesis",		// 22
		"expecting left bracket",		// 23
		"expecting right bracket",		// 24
		"expecting pointer arrow",		// 25
		"expecting declaration",		// 26
		"expecting identifier",			// 27
		"expecting class name",			// 28
		"expecting field name",			// 29
		"expecting type name",			// 30
		"expecting constant",			// 31
		"expecting integer constant",		// 32
		"expecting expression",			// 33
		"expecting statement",			// 34
		"name redefinition: ",			// 35
		"illegal array declaration",		// 36
		"reference expression on non reference",// 37
		"pointer deref on non pointer",		// 38
		"field on non class",			// 39
		"subscript on non array",		// 40
		"index expression not integer type",    // 41
		"unknown name: ",			// 42
		"expression must be boolean",		// 43
		"type mismatch",			// 44
		"function call on non function",	// 45
		"value must be numeric",		// 46
		""
		};

	public ParseException (int i) { super(messages[i]); }
	public ParseException (int i, String name) { super(messages[i] + name); }
	public ParseException () { super(); }
}
