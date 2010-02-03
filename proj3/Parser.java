//
//	parser skeleton, CS 480/580, Winter 2001
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if (debug) System.out.println("start " + n + 
			" token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + 
			" token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void program () throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		stop("program");
	}

	private void declaration () throws ParseException {
		start("declaration");
		if (lex.match("class"))
			classDeclaration();
		else if (lex.match("function") || lex.match("const") 
			|| lex.match("var") || lex.match("type"))
			nonClassDeclaration();
		else 
			parseError(26);
		stop("declaration");
		}

	private void nonClassDeclaration () throws ParseException {
		start("nonClassDeclaration");
		if (lex.match("function"))
			functionDeclaration();
		else if (lex.match("const") || lex.match("var") 
				|| lex.match("type"))
			nonFunctionDeclaration();
		else
			parseError(26);
		stop("nonClassDeclaration");
		}

	private void nonFunctionDeclaration () throws ParseException {
		start("nonFunctionDeclaration");
		if (lex.match("var"))
			variableDeclaration();
		else if (lex.match("const"))
			constantDeclaration();
		else if (lex.match("type"))
			typeDeclaration();
		else 
			parseError(26);
		stop("nonFunctionDeclaration");
		}

	private void constantDeclaration () throws ParseException {
		start("constantDeclaration");
		if (lex.match("const")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			if (lex.tokenCategory() == lex.intToken)
				;
			else if (lex.tokenCategory() == lex.realToken)
				;
			else if (lex.tokenCategory() == lex.stringToken)
				;
			else
				parseError(31);
			lex.nextLex();
			}
		else
			parseError(6);
		stop("constantDeclaration");
		}

	private void typeDeclaration () throws ParseException {
		start("typeDeclaration");
		if (lex.match("type")) {
			lex.nextLex();
			nameDeclaration();
		} else
			parseError(14); 
		stop("typeDeclaration");
	}

	private void variableDeclaration () throws ParseException {
		start("variableDeclaration");
		if (lex.match("var")) {
			lex.nextLex();
			nameDeclaration();
			}
		else
			parseError(15);
		stop("variableDeclaration");
		}

	private void nameDeclaration () throws ParseException {
		start("nameDeclaration");
		if (! lex.isIdentifier()) 
			parseError(27);
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		type();
		stop("nameDeclaration");
		}

	private void classDeclaration() throws ParseException {
		start("classDeclaration");
		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		lex.nextLex();
		classBody();
		stop("classDeclaration");
		}

	private void classBody() throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonClassDeclaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		lex.nextLex();
		stop("classBody");
		}

	private void functionDeclaration() throws ParseException {
		start("functionDeclaration");
		if (! lex.match("function"))
			parseError(10);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		lex.nextLex();
		arguments();
		returnType();
		functionBody();
		stop("functionDeclaration");
		}
		
	private void arguments () throws ParseException {
		start("arguments");
		if (! lex.match("("))
			parseError(21);
		lex.nextLex();
		argumentList();
		if (! lex.match(")"))
			parseError(22);
		lex.nextLex();
		stop("arguments");
		}

	private void argumentList () throws ParseException {
		start("argumentList");
		if (lex.isIdentifier()) {
			nameDeclaration();
			while (lex.match(",")) {
				lex.nextLex();
				nameDeclaration();
				}
			}
		stop("argumentList");
		}

	private void returnType () throws ParseException {
		start("returnType");
		if (lex.match(":")) {
			lex.nextLex();
			type();
			}
		stop("returnType");
		}

	private void type () throws ParseException {
		start("type");
		if (lex.isIdentifier()) {
			lex.nextLex();
			}
		else if (lex.match("^")) {
			lex.nextLex();
			type();
			}
		else if (lex.match("[")) {
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();
			type();
			}
		else
			parseError(30);
		stop("type");
		}

	private void functionBody () throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonClassDeclaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		compoundStatement();
		stop("functionBody");
		}

	private void compoundStatement () throws ParseException {
		start("compoundStatement");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			statement();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
			}
		lex.nextLex();
		stop("compoundStatement");
		}

	private void statement () throws ParseException {
		start("statement");
		if (lex.match("return"))
			returnStatement();
		else if (lex.match("if"))
			ifStatement();
		else if (lex.match("while"))
			whileStatement();
		else if (lex.match("begin"))
			compoundStatement();
		else if (lex.isIdentifier())
			assignOrFunction();
		else
			parseError(34);
		stop("statement");
		}

	private boolean firstExpression() {
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

	private void returnStatement () throws ParseException {
		start("returnStatement");
		if (! lex.match("return"))
			parseError(12);
		lex.nextLex();
		if (lex.match("(")) {
			lex.nextLex();
			expression();
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		stop("returnStatement");
		}

	private void ifStatement () throws ParseException {
		start("ifStatement");
		if (! lex.match("if"))
			parseError(11);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression();
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement();
		if (lex.match("else")) {
			lex.nextLex();
			statement();
			}
		stop("ifStatement");
		}

	private void whileStatement () throws ParseException {
		start("whileStatement");
		if (! lex.match("while"))
			parseError(16);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression();
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement();
		stop("whileStatement");
		}

	private void assignOrFunction () throws ParseException {
		start("assignOrFunction");
		reference();
		if (lex.match("=")) {
			lex.nextLex();
			expression();
			}
		else if (lex.match("(")) {
			lex.nextLex();
			parameterList();
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		else
			parseError(20);
		stop("assignOrFunction");
		}

	private void parameterList () throws ParseException {
		start("parameterList");
		if (firstExpression()) {
			expression();
			while (lex.match(",")) {
				lex.nextLex();
				expression();
				}
			}
		stop("parameterList");
		}

	private void expression () throws ParseException {
		start("expression");
		relExpression();
		while (lex.match("and") || lex.match("or")) {
			lex.nextLex();
			relExpression();
			}
		stop("expression");
		}

	private boolean relOp() {
		if (lex.match("<") || lex.match("<=") ||
			lex.match("==") || lex.match("!=") ||
				lex.match(">") || lex.match(">="))
				return true;
		return false;
		}

	private void relExpression () throws ParseException {
		start("relExpression");
		plusExpression();
		if (relOp()) {
			lex.nextLex();
			plusExpression();
			}
		stop("relExpression");
		}

	private void plusExpression () throws ParseException {
		start("plusExpression");
		timesExpression();
		while (lex.match("+") || lex.match("-") || lex.match("<<")) {
			lex.nextLex();
			timesExpression();
			}
		stop("plusExpression");
		}

	private void timesExpression () throws ParseException {
		start("timesExpression");
		term();
		while (lex.match("*") || lex.match("/") || lex.match("%")) {
			lex.nextLex();
			term();
			}
		stop("timesExpression");
		}

	private void term () throws ParseException {
		start("term");
		if (lex.match("(")) {
			lex.nextLex();
			expression();
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		else if (lex.match("not")) {
			lex.nextLex();
			term();
			}
		else if (lex.match("new")) {
			lex.nextLex();
			type();
			}
		else if (lex.match("-")) {
			lex.nextLex();
			term();
			}
		else if (lex.match("&")) {
			lex.nextLex();
			reference();
			}
		else if (lex.tokenCategory() == lex.intToken) {
			lex.nextLex();
			}
		else if (lex.tokenCategory() == lex.realToken) {
			lex.nextLex();
			}
		else if (lex.tokenCategory() == lex.stringToken) {
			lex.nextLex();
			}
		else if (lex.isIdentifier()) {
			reference();
			if (lex.match("(")) {
				lex.nextLex();
				parameterList();
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
				}
			}
		else
			parseError(33);
		stop("term");
		}

	private void reference () throws ParseException {
		start("reference");
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
				expression();
				if (! lex.match("]"))
					parseError(24);
				lex.nextLex();
				}
			}
		stop("reference");
		}

}
