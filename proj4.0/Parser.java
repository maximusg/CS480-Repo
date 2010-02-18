//
//	parser skeleton, CS 480/580, Winter 2001
//	written by Tim Budd
//		modified by: Brad Kessler, Sarah Clisby, Richard Tracy, Max Geiszler
//

public class Parser {
	private Lexer lex;
	private boolean debug;
	
	
	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		SymbolTable sym = new GlobalSymbolTable();
		lex.nextLex();
		
		sym.enterType("int", PrimitiveType.IntegerType);
		sym.enterType("real", PrimitiveType.RealType);

		sym.enterFunction("printInt", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printReal", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printStr", new FunctionType(PrimitiveType.VoidType));
		
		program(sym);
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if(debug) System.out.println("start " + n + 
			" token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + 
			" token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void program (SymbolTable sym) throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		stop("program");
	}

	private void declaration (SymbolTable sym) throws ParseException {
		start("declaration");
		if (lex.match("class"))
			classDeclaration(sym);
		else if (lex.match("function") || lex.match("const") 
			|| lex.match("var") || lex.match("type"))
			nonClassDeclaration(sym);
		else 
			parseError(26);
		stop("declaration");
		}

	private void nonClassDeclaration (SymbolTable sym) throws ParseException {
		start("nonClassDeclaration");
		if (lex.match("function"))
			functionDeclaration(sym);
		else if (lex.match("const") || lex.match("var") 
				|| lex.match("type"))
			nonFunctionDeclaration(sym);
		else
			parseError(26);
		stop("nonClassDeclaration");
		}

	private void nonFunctionDeclaration (SymbolTable sym) throws ParseException {
		start("nonFunctionDeclaration");
		if (lex.match("var"))
			variableDeclaration(sym);
		else if (lex.match("const"))
			constantDeclaration(sym);
		else if (lex.match("type"))
			typeDeclaration(sym);
		else 
			parseError(26);
		stop("nonFunctionDeclaration");
		}

	private void constantDeclaration (SymbolTable sym) throws ParseException {
		start("constantDeclaration");
		if (lex.match("const")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			if (lex.tokenCategory() == lex.intToken){
				Ast intVar = new IntegerNode(new Integer(lex.tokenText()));
				sym.enterConstant(lex.tokenText(), intVar);
			}else if (lex.tokenCategory() == lex.realToken){
				Ast realVar = new RealNode(new Double(lex.tokenText()));
				sym.enterConstant(lex.tokenText(), realVar);
			}else if (lex.tokenCategory() == lex.stringToken){
				Ast stringVar = new StringNode(lex.tokenText());
				sym.enterConstant(lex.tokenText(), stringVar);
			}else
				parseError(31);
			lex.nextLex();
			}
		else
			parseError(6);
		stop("constantDeclaration");
		}

	// This needs to be changed to accommodate type
	private void typeDeclaration (SymbolTable sym) throws ParseException {
		start("typeDeclaration");
		if (lex.match("type")) {
			lex.nextLex();
			String s = lex.tokenText();
			Type result = nameDeclaration(sym);
			sym.enterType (s, result );
			//lex.nextLex();
		} else
			parseError(14); 
		stop("typeDeclaration");
	}

	private void variableDeclaration (SymbolTable sym) throws ParseException {
		start("variableDeclaration");
		if (lex.match("var")) {
			lex.nextLex();
			String s = lex.tokenText();
			Type t = nameDeclaration(sym);
			sym.enterVariable(s,t);
			}
		else
			parseError(15);
		stop("variableDeclaration");
		}


	private Type nameDeclaration (SymbolTable sym) throws ParseException {

		start("nameDeclaration");
		if (! lex.isIdentifier()) 
			parseError(27);
		String s = lex.tokenText();
		if(sym.nameDefined(s)){
			throw new ParseException(35,s);
		}
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		Type result = type(sym);
		if(sym instanceof GlobalSymbolTable)
			CodeGen.genGlobal(s, result.size());
		//sym.enterVariable(s,result);
		stop("nameDeclaration");
		return result;
		
		}

	private void classDeclaration(SymbolTable sym) throws ParseException {
		start("classDeclaration");
		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String s = lex.tokenText();
		lex.nextLex();
		ClassSymbolTable cst = new ClassSymbolTable((GlobalSymbolTable)sym);
		classBody(cst);
		sym.enterType(s, new ClassType(cst));
		stop("classDeclaration");
		}

	private void classBody(SymbolTable sym) throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonClassDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		lex.nextLex();
		stop("classBody");
		}

	private void functionDeclaration(SymbolTable sym) throws ParseException {
		start("functionDeclaration");
		if (! lex.match("function"))
			parseError(10);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String s =  lex.tokenText();
		lex.nextLex();
		FunctionSymbolTable fst = new FunctionSymbolTable((GlobalSymbolTable) sym);
		fst.doingArguments = true;
		arguments(fst);
		Type t = returnType(sym);
		fst.doingArguments = false;
		functionBody(fst,s);
		sym.enterFunction(s, new FunctionType(t));
		stop("functionDeclaration");
		}
		
	private void arguments (SymbolTable sym) throws ParseException {
		start("arguments");
		if (! lex.match("("))
			parseError(21);
		lex.nextLex();
		argumentList(sym);
		if (! lex.match(")"))
			parseError(22);
		lex.nextLex();
		stop("arguments");
		}

	private void argumentList (SymbolTable sym) throws ParseException {
		start("argumentList");
		if (lex.isIdentifier()) {
			nameDeclaration(sym);
			while (lex.match(",")) {
				lex.nextLex();
				nameDeclaration(sym);
				}
			}
		stop("argumentList");
		}

	private Type returnType (SymbolTable sym) throws ParseException {
		Type result = null;
		start("returnType");
		if (lex.match(":")) {
			lex.nextLex();
			result = type(sym);
		}else{
			result = PrimitiveType.VoidType;
		}
		stop("returnType");
		return result;
		}

	private Type type (SymbolTable sym) throws ParseException {
		start("type");
		Type result = null;
		if (lex.isIdentifier()) {
			result = sym.lookupType(lex.tokenText());
			lex.nextLex();
			}
		else if (lex.match("^")) {
			lex.nextLex();
			result = new PointerType(type(sym));
			}
		else if (lex.match("[")) {
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int lower = (new Integer(lex.tokenText()).intValue());
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int upper = (new Integer(lex.tokenText()).intValue());
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();
			result = type(sym);
			result = new ArrayType(lower, upper, result);
			}
		else
			parseError(30);
		stop("type");
		return result;
		}

	private void functionBody (SymbolTable sym, String name) throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonClassDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		CodeGen.genProlog(name,sym.size());
		compoundStatement(sym);
		CodeGen.genEpilog(name);
		stop("functionBody");
		}

	private void compoundStatement (SymbolTable sym) throws ParseException {
		start("compoundStatement");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			statement(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
			}
		lex.nextLex();
		stop("compoundStatement");
		}

	private void statement (SymbolTable sym) throws ParseException {
		start("statement");
		if (lex.match("return"))
			returnStatement(sym);
		else if (lex.match("if"))
			ifStatement(sym);
		else if (lex.match("while"))
			whileStatement(sym);
		else if (lex.match("begin"))
			compoundStatement(sym);
		else if (lex.isIdentifier())
			assignOrFunction(sym);
		else
			parseError(34);
		stop("statement");
		}

	private boolean firstExpression(SymbolTable sym) {
		if (lex.match("(") || lex.match("not") || lex.match("-") || lex.match("&"))
			return true;
		if (lex.isIdentifier())
			return true;
		if ((lex.tokenCategory() == lex.intToken) ||
			(lex.tokenCategory() == lex.realToken) ||
			(lex.tokenCategory() == lex.stringToken))
			return true;
		return false;
		}

	private void returnStatement (SymbolTable sym) throws ParseException {
		start("returnStatement");
		if (! lex.match("return"))
			parseError(12);
		lex.nextLex();
		if (lex.match("(")) {
			lex.nextLex();
			expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		stop("returnStatement");
		}

	private void ifStatement (SymbolTable sym) throws ParseException {
		start("ifStatement");
		if (! lex.match("if"))
			parseError(11);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement(sym);
		if (lex.match("else")) {
			lex.nextLex();
			statement(sym);
			}
		stop("ifStatement");
		}

	private void whileStatement (SymbolTable sym) throws ParseException {
		start("whileStatement");
		if (! lex.match("while"))
			parseError(16);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement(sym);
		stop("whileStatement");
		}

	private void assignOrFunction (SymbolTable sym) throws ParseException {
		start("assignOrFunction");
		Ast leftAst = reference(sym);
		if (lex.match("=")) {
			lex.nextLex();
			Ast rightAst = expression(sym);
			if (!(leftAst.type instanceof AddressType))
				throw new ParseException(37, "Left ast not an address type");
			
			if (!(rightAst.type == ((AddressType)leftAst.type).baseType))
				throw new ParseException(44, "Right ast does not match the basetype of the left ast");
			
			CodeGen.genAssign(leftAst, rightAst);
			}
		else if (lex.match("(")) {
			lex.nextLex();
			parameterList(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		else
			parseError(20);
		stop("assignOrFunction");
		}

	private void parameterList (SymbolTable sym) throws ParseException {
		start("parameterList");
		if (firstExpression(sym)) {
			expression(sym);
			while (lex.match(",")) {
				lex.nextLex();
				expression(sym);
				}
			}
		stop("parameterList");
		}

	private Ast expression (SymbolTable sym) throws ParseException {
		start("expression");
		Ast indexExpression = relExpression(sym);
		while (lex.match("and") || lex.match("or")){
			MustBeBoolean(indexExpression);
			String text = lex.tokenText();
			lex.nextLex();
			Ast result = relExpression(sym);
			if(text.equals("and")){
				indexExpression = new BinaryNode(BinaryNode.and, PrimitiveType.BooleanType, indexExpression, result);
			}else{
				indexExpression = new BinaryNode(BinaryNode.or, PrimitiveType.BooleanType, indexExpression, result);
			}
		}
		stop("expression");
		return indexExpression;
		}

	private int relOp(SymbolTable sym) {
		int result = -1;
		if (lex.match("<")){
			result = BinaryNode.less;
		}else if(lex.match("<=")){
			result = BinaryNode.lessEqual;
		}else if(lex.match("==")){
			result = BinaryNode.equal;
		}else if (lex.match("!=")){
			result = BinaryNode.notEqual;
		}else if (lex.match(">")){
			result = BinaryNode.greater;
		}else if(lex.match(">=")){
			result = BinaryNode.greaterEqual;
		}
				
		return result;
		}

	private Ast relExpression (SymbolTable sym) throws ParseException {
		start("relExpression");
		Ast indexExpression = plusExpression(sym);
		int BNresult = relOp(sym);
		if (BNresult > 0) {
			lex.nextLex();
			Ast result = plusExpression(sym);
		    if (! indexExpression.type.equals(result.type)){
			    parseError(44);
		     }
			indexExpression = new BinaryNode(BNresult, PrimitiveType.BooleanType, indexExpression, result);
			}
		stop("relExpression");
		return indexExpression;
		}

	private Ast plusExpression (SymbolTable sym) throws ParseException {
		start("plusExpression");
		Ast indexExpression = timesExpression(sym);
		while (lex.match("+") || lex.match("-") || lex.match("<<")) {
			if(lex.match("<<")){
				lex.nextLex();
				Ast result = timesExpression(sym);
				isInteger(indexExpression);
				isInteger(result);
				indexExpression = new BinaryNode(BinaryNode.leftShift, sym.lookupType("int"), indexExpression, result);
				
			}else if((lex.match("+"))||(lex.match("-"))){
				lex.nextLex();
				Ast result = timesExpression(sym);
				indexExpression = convertMe(indexExpression,result);
				result = convertMe(result,indexExpression);
				isNumeric(indexExpression);
				isNumeric(result);
				if(((indexExpression.type.equals(PrimitiveType.IntegerType))&&(result.type.equals(PrimitiveType.IntegerType)))||((indexExpression.type.equals(PrimitiveType.RealType))&&(result.type.equals(PrimitiveType.RealType)))){
					parseError(44);
				}
				if((lex.match("-"))){
					indexExpression = new BinaryNode(BinaryNode.minus, indexExpression.type, indexExpression, result);
				}else{
					indexExpression = new BinaryNode(BinaryNode.plus, indexExpression.type, indexExpression, result);
				}
			}
			
			
			}
		stop("plusExpression");
		return indexExpression;
		}
	private Ast convertMe(Ast a,Ast b){
		if((a.type.equals(PrimitiveType.IntegerType))&& (b.type.equals(PrimitiveType.RealType))){
			a = new UnaryNode(UnaryNode.convertToReal,PrimitiveType.RealType,a);
		}
		return a;
	}
	private void isInteger(Ast test)throws ParseException{
		if(!(test.type.equals(PrimitiveType.IntegerType) )){
			parseError(41);
		}	
	}
	
	private void isNumeric(Ast test) throws ParseException{
		if(!(test.type.equals(PrimitiveType.IntegerType) )&&(!test.type.equals(PrimitiveType.RealType))){
			parseError(46);
		}
		
	}
	
	private Ast timesExpression (SymbolTable sym) throws ParseException {
		start("timesExpression");
		Ast indexExpression = term(sym);
		while (lex.match("*") || lex.match("/") || lex.match("%")) {
			if(lex.match("%")){
				lex.nextLex();
				Ast result = term(sym);
				isInteger(indexExpression);
				isInteger(result);
				indexExpression = new BinaryNode(BinaryNode.remainder, PrimitiveType.IntegerType, indexExpression, result);
				
			}else if((lex.match("*"))||(lex.match("/"))){
				lex.nextLex();
				Ast result = term(sym);	
				indexExpression = convertMe(indexExpression,result);
				result = convertMe(result,indexExpression);
				isNumeric(indexExpression);
				isNumeric(result);
				if(((indexExpression.type.equals(PrimitiveType.IntegerType))&&(result.type.equals(PrimitiveType.IntegerType)))||((indexExpression.type.equals(PrimitiveType.RealType))&&(result.type.equals(PrimitiveType.RealType)))){
					parseError(44);
				}
				if((lex.match("*"))){
					indexExpression = new BinaryNode(BinaryNode.times, indexExpression.type, indexExpression, result);
				}else{
					indexExpression = new BinaryNode(BinaryNode.divide, indexExpression.type, indexExpression, result);
				}
			}

		}
		stop("timesExpression");
		return indexExpression;
		}

	private Ast term (SymbolTable sym) throws ParseException {
		start("term");
		Ast indexExpression = null;
		if (lex.match("(")) {
			lex.nextLex();
			indexExpression = expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		else if (lex.match("not")) {
			lex.nextLex();
			indexExpression = term(sym);
			MustBeBoolean(indexExpression);
			indexExpression = new UnaryNode(UnaryNode.notOp,indexExpression.type,indexExpression);
			}
		else if (lex.match("new")) {
			lex.nextLex();
			Type t = type(sym);
			int size = t.size();
			indexExpression = new UnaryNode(UnaryNode.newOp,new PointerType(t),new IntegerNode(size));
			}
		else if (lex.match("-")) {
			lex.nextLex();
			indexExpression = term(sym);
			isNumeric(indexExpression);
			indexExpression = new UnaryNode(UnaryNode.negation,indexExpression.type,indexExpression);
			}
		else if (lex.match("&")) {
			lex.nextLex();
			indexExpression = reference(sym);
			indexExpression = new UnaryNode(UnaryNode.dereference,new PointerType(indexExpression.type),indexExpression);
			}
		else if (lex.tokenCategory() == lex.intToken) {
			lex.nextLex();
			indexExpression = new IntegerNode(new Integer(lex.tokenText()));
			}
		else if (lex.tokenCategory() == lex.realToken) {
			lex.nextLex();
			}
		else if (lex.tokenCategory() == lex.stringToken) {
			lex.nextLex();
			}
		else if (lex.isIdentifier()) {
			reference(sym);
			if (lex.match("(")) {
				lex.nextLex();
				parameterList(sym);
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
				}
			}
		else
			parseError(33);
		stop("term");
		return indexExpression;
		}

	private Ast reference (SymbolTable sym) throws ParseException {
		start("reference");
		Ast indexExpression = null;
		if (! lex.isIdentifier())
			parseError(27);
		lex.nextLex();
		while (lex.match("^") || lex.match(".") || lex.match("[")) {
			if (lex.match("^")) {
				lex.nextLex();
				}
			else if (lex.match(".")) {
				lex.nextLex();
				if (! lex.isIdentifier())
					parseError(27);
				lex.nextLex();
				}
			else {
				lex.nextLex();
				indexExpression = expression(sym);
				if (! lex.match("]"))
					parseError(24);
				lex.nextLex();
				}
			}
		
		stop("reference");
		return indexExpression;
		}

	private void MustBeBoolean(Ast a) throws ParseException{
		if(!a.type.equals(PrimitiveType.BooleanType)){
			parseError(43);
		}
	}
}
