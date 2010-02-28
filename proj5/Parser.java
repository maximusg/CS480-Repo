//
//	parser skeleton, CS 480/580, Winter 1998
//	written by Tim Budd
//		modified by:
//

import java.util.Vector;

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		SymbolTable sym = new GlobalSymbolTable();
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
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			Ast value = null;
			if (lex.tokenCategory() == lex.intToken)
				value = new IntegerNode(new Integer(lex.tokenText()));
			else if (lex.tokenCategory() == lex.realToken)
				value = new RealNode(new Double(lex.tokenText()));
			else if (lex.tokenCategory() == lex.stringToken)
				value = new StringNode(lex.tokenText());
			else
				parseError(31);
			sym.enterConstant(name, value);
			lex.nextLex();
			}
		else
			parseError(6);
		stop("constantDeclaration");
		}

	private void typeDeclaration (SymbolTable sym) throws ParseException {
		start("typeDeclaration");
		if (lex.match("type")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			sym.enterType(name, type(sym));
		} else
			parseError(14); 
		stop("typeDeclaration");
	}

	private void variableDeclaration (SymbolTable sym) throws ParseException {
		start("variableDeclaration");
		if (lex.match("var")) {
			lex.nextLex();
			nameDeclaration(sym);
			}
		else
			parseError(15);
		stop("variableDeclaration");
		}

	private void nameDeclaration (SymbolTable sym) throws ParseException {
		start("nameDeclaration");
		if (! lex.isIdentifier()) 
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		Type t = type(sym);
		sym.enterIdentifier(name, t);
		if (sym instanceof GlobalSymbolTable)
			CodeGen.genGlobal(name, t.size());
		stop("nameDeclaration");
		}

	private void classDeclaration(SymbolTable sym) throws ParseException {
		start("classDeclaration");
		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		SymbolTable csym = new ClassSymbolTable(sym);
		sym.enterType(name, new ClassType(csym));
		classBody(csym);
		stop("classDeclaration");
		}

	private void classBody(SymbolTable sym) throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonFunctionDeclaration(sym);
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
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		FunctionSymbolTable fsym = new FunctionSymbolTable(sym);
		arguments(fsym);
		fsym.doingArguments = false;
		Type rt = returnType(sym);
		sym.enterFunction(name, new FunctionType(rt));
		functionBody(fsym, name);
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
		start("returnType");
		Type result = PrimitiveType.VoidType;
		if (lex.match(":")) {
			lex.nextLex();
			result = type(sym);
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
			int lower = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int upper = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();
			result = new ArrayType(lower, upper, type(sym));
			}
		else
			parseError(30);
		stop("type");
		return result;
		}

	private void functionBody (SymbolTable sym, String name) throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonFunctionDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		CodeGen.genProlog(name, sym.size());
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

	private void returnStatement (SymbolTable sym) throws ParseException {
		start("returnStatement");
		if (! lex.match("return"))
			parseError(12);
		lex.nextLex();
		Ast result = null;
		if (lex.match("(")) {
			lex.nextLex();
			result = expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		CodeGen.genReturn(result);
		stop("returnStatement");
		}

	private void ifStatement (SymbolTable sym) throws ParseException {
		start("ifStatement");
		if (! lex.match("if"))
			parseError(11);
		lex.nextLex();
		if (lex.match("("))
			lex.nextLex();
		else
			throw new ParseException(21); 
		Ast test = expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		/* if (lex.match("then"))
			lex.nextLex();
		else
			throw new ParseException(13); */
		mustBeBoolean(test);
		Label lab1 = new Label();
		test.branchIfFalse(lab1);
		statement(sym);
		if (lex.match("else")) {
			lex.nextLex();
			Label lab2 = new Label();
			lab2.genBranch();
			lab1.genCode();
			statement(sym);
			lab2.genCode();
		} else {
			lab1.genCode();
		}
		stop("ifStatement");
		}

	private void whileStatement (SymbolTable sym) throws ParseException {
		start("whileStatement");
		if (! lex.match("while"))
			parseError(16);
		lex.nextLex();
		if (lex.match("("))
			lex.nextLex();
		else
			throw new ParseException(21); 
		Ast test = expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		/*if (lex.match("do"))
			lex.nextLex();
		else
			throw new ParseException(7); */
		mustBeBoolean(test);
		Label lab1 = new Label();
		Label lab2 = new Label();
		lab1.genCode();
		test.branchIfFalse(lab2);
		statement(sym);
		lab1.genBranch();
		lab2.genCode();
		stop("whileStatement");
		}

	private void assignOrFunction (SymbolTable sym) throws ParseException {
		start("assignOrFunction");
		Ast val = reference(sym);
		if (lex.match("=")) {
			lex.nextLex();
			Ast right = expression(sym);
			Type lt = addressBaseType(val.type);
			if (! lt.equals(right.type))
				parseError(44);
			CodeGen.genAssign(val, right);
			}
		else if (lex.match("(")) {
			if (! (val.type instanceof FunctionType))
				parseError(45);
			lex.nextLex();
			Vector args = parameterList(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			val = new FunctionCallNode(val, args);
			val.genCode();
			}
		else
			parseError(20);
		    stop("assignOrFunction");
		    }

	    private Vector parameterList (SymbolTable sym) throws ParseException {
		    start("parameterList");
		    Vector args = new Vector();
		    if (firstExpression()) {
			    args.addElement(expression(sym));
			    while (lex.match(",")) {
				    lex.nextLex();
				    args.addElement(expression(sym));
				    }
			    }
		    stop("parameterList");
		    return args;
		    }

	    private void mustBeBoolean(Ast v) throws ParseException {
		    if (! v.type.equals(PrimitiveType.BooleanType))
			    parseError(43);;
	    }

	    private Ast expression (SymbolTable sym) throws ParseException {
		    start("expression");
		    Ast result = relExpression(sym);
		    while (lex.match("and") || lex.match("or")) {
			    String op = lex.tokenText();
			    mustBeBoolean(result);
			    lex.nextLex();
			    Ast right = relExpression(sym);
			    mustBeBoolean(right);
			    if (op.equals("and"))
				    result = new BinaryNode(BinaryNode.and,
					    result.type, result, right);
			    else
				    result = new BinaryNode(BinaryNode.or,
					    result.type, result, right);
			    }
		    stop("expression");
		    return result;
		    }

	    private int relOp() {
		    if (lex.match("<")) return BinaryNode.less;
		    if (lex.match("<=")) return BinaryNode.lessEqual;
		    if (lex.match("==")) return BinaryNode.equal;
		    if (lex.match("!=")) return BinaryNode.notEqual;
		    if (lex.match(">")) return BinaryNode.greater;
		    if (lex.match(">=")) return BinaryNode.greaterEqual;
		    return 0;
		    }

	    private Ast relExpression (SymbolTable sym) throws ParseException {
		    start("relExpression");
		    Ast result = plusExpression(sym);
		    int op = relOp();
		    if (op != 0) {
			    lex.nextLex();
			    Ast right = plusExpression(sym);
			    if (! result.type.equals(right.type))
				    parseError(44);
			    result = new BinaryNode(op, 
				    PrimitiveType.BooleanType, result, right);
			    }
		    stop("relExpression");
		    return result;
		    }

	    private Ast checkConversion (Ast left, Ast right) {
		    if (left.type.equals(PrimitiveType.IntegerType) &&
			    right.type.equals(PrimitiveType.RealType))
			    return new UnaryNode(UnaryNode.convertToReal,
				    PrimitiveType.RealType, left);
		    return left;
	    }

	    private void mustBeNumeric (Ast left) throws ParseException {
		    Type t = left.type;
		    if (t.equals(PrimitiveType.IntegerType)) return;
		    if (t.equals(PrimitiveType.RealType)) return;
		    parseError(46);
	    }

	    private int plusOp () {
		    if (lex.match("+")) return BinaryNode.plus;
		    if (lex.match("-")) return BinaryNode.minus;
		    if (lex.match("<<")) return BinaryNode.leftShift;
		    return 0;
	    }

	    private Ast plusExpression (SymbolTable sym) throws ParseException {
		    start("plusExpression");
		    Ast result = timesExpression(sym);
		    int op = 0;
		    while ((op = plusOp()) != 0) {
			    lex.nextLex();
			    Ast right = timesExpression(sym);
			    if (op == BinaryNode.leftShift) {
				    mustBeInteger(result);
				    mustBeInteger(right);
			    } else {
				    result = checkConversion(result, right);
				    right = checkConversion(right, result);
				    mustBeNumeric(result);
				    mustBeNumeric(right);
				    if (! result.type.equals(right.type))
					    parseError(44);
			    }
			    result = new BinaryNode(op, result.type, result, right);
			    }
		    stop("plusExpression");
		    return result;
		    }

	    private int timesOp () {
		    if (lex.match("*")) return BinaryNode.times;
		    if (lex.match("/")) return BinaryNode.divide;
		    if (lex.match("%")) return BinaryNode.remainder;
		    return 0;
	    }

	    private void mustBeInteger (Ast v) throws ParseException {
		    if (! v.type.equals(PrimitiveType.IntegerType))
			    parseError(41);
	    }

	    private Ast timesExpression (SymbolTable sym) throws ParseException {
		    start("timesExpression");
		    Ast result = term(sym);
		    int op = 0;
		    while ((op = timesOp()) != 0) {
			    lex.nextLex();
			    Ast right = term(sym);
			    if (op == BinaryNode.remainder) {
				    mustBeInteger(result);
				    mustBeInteger(right);
			    } else {
				    result = checkConversion(result, right);
				    right = checkConversion(right, result);
				    mustBeNumeric(result);
				    mustBeNumeric(right);
				    if (! result.type.equals(right.type))
					    parseError(44);
			    }
			    result = new BinaryNode(op, result.type, result, right);
			    }
		    stop("timesExpression");
		    return result;
		    }

	    private Ast term (SymbolTable sym) throws ParseException {
		    start("term");
		    Ast result = null;
		    if (lex.match("(")) {
			    lex.nextLex();
			    result = expression(sym);
			    if (! lex.match(")"))
				    parseError(22);
			    lex.nextLex();
			    }
		    else if (lex.match("not")) {
			    lex.nextLex();
			    result = term(sym);
			    mustBeBoolean(result);
			    result = new UnaryNode(UnaryNode.notOp,
				    result.type, result);
			    }
		    else if (lex.match("new")) {
			    lex.nextLex();
			    Type t = type(sym);
			    result = new UnaryNode(UnaryNode.newOp, new PointerType(t), new IntegerNode(t.size()));
			}
		    else if (lex.match("-")) {
			    lex.nextLex();
			    result = term(sym);
			    mustBeNumeric(result);
			    result = new UnaryNode(UnaryNode.negation,
				    result.type, result);
			    }
		    else if (lex.match("&")) {
			    lex.nextLex();
			    result = reference(sym);
			    result.type = new PointerType(
				    addressBaseType(result.type));
			    }
		    else if (lex.tokenCategory() == lex.intToken) {
			    result = new IntegerNode(new Integer(lex.tokenText()));
			    lex.nextLex();
			    }
		    else if (lex.tokenCategory() == lex.realToken) {
			    result = new RealNode(new Double(lex.tokenText()));
			    lex.nextLex();
			    }
		    else if (lex.tokenCategory() == lex.stringToken) {
			    result = new StringNode(lex.tokenText());
			    lex.nextLex();
			    }
		    else if (lex.isIdentifier()) {
			    result = reference(sym);
			    if (lex.match("(")) {
				    if (! (result.type instanceof FunctionType))
					parseError(45);
				lex.nextLex();
				Vector args = parameterList(sym);
				result = new FunctionCallNode(result, args);
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
			} else {
				if (result.type instanceof AddressType)
					result = new UnaryNode(
						UnaryNode.dereference,
						addressBaseType(result.type),
						result);
			}
		} else
			parseError(33);
		stop("term");
		return result;
		}

	private Type addressBaseType(Type t) throws ParseException {
		if (! (t instanceof AddressType))
			parseError(37);
		AddressType at = (AddressType) t;
		return at.baseType;
	}

	private Ast reference (SymbolTable sym) throws ParseException {
		start("reference");
		Ast result = null;
		if (! lex.isIdentifier())
			parseError(27);
		result = sym.lookupName(new FramePointer(), lex.tokenText());
		lex.nextLex();
		while (lex.match("^") || lex.match(".") || lex.match("[")) {
			if (lex.match("^")) {
				Type b = addressBaseType(result.type);
				if ( !(b instanceof PointerType) )
					parseError(38);
				PointerType pb = (PointerType) b;
				result = new UnaryNode(UnaryNode.dereference,
					new AddressType(pb.baseType), result);
				lex.nextLex();
				}
			else if (lex.match(".")) {
				lex.nextLex();
				if (! lex.isIdentifier())
					parseError(27);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ClassType) )
					parseError(39);
				ClassType pb = (ClassType) b;
				if (! pb.symbolTable.nameDefined(lex.tokenText()))
				   throw new ParseException(29);
				result = pb.symbolTable.lookupName(result, lex.tokenText());
				lex.nextLex();
				}
			else {
				lex.nextLex();
				Ast indexExpression = expression(sym);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ArrayType) )
					parseError(40);
				ArrayType at = (ArrayType) b;
				if (! indexExpression.type.equals(
					PrimitiveType.IntegerType))
						parseError(41);
				indexExpression = new BinaryNode(
					BinaryNode.minus, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.lowerBound));
				indexExpression = new BinaryNode(
					BinaryNode.times, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.elementType.size()));
				result = new BinaryNode(
					BinaryNode.plus, 
					new AddressType(at.elementType),
					result,
					indexExpression);
				if (! lex.match("]"))
					parseError(24);
				lex.nextLex();
				}
			}
		stop("reference");
		return result;
		}

}
