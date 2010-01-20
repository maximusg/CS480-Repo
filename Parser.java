//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by: Richard Tracy, Brad Kessler, Sarah Clisby
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
		if (debug) System.out.println("start " + n + " token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + " token: " + lex.tokenText());
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

	private void declaration(){
		try{
		start("declaration");
			if((lex.match("function"))||(lex.match("var"))||(lex.match("type"))||(lex.match("const"))){
				nonClassDeclaration();
			}else if((lex.match("class"))){
				classDeclaration();
			}else{
				throw new ParseException(26); // This is the "expecting declaration" exception--is this correct?
			}
		stop("declaration");
		}catch (ParseException e){
			
		}
	}

	private void constantDeclaration()throws ParseException{
		start("constantDeclaration");
		if(lex.match("const")){
			lex.nextLex();
			if(lex.isIdentifier()){
				lex.nextLex();
				if(lex.match("=")){
					//need to put constant rules in here or a call to constant function
					lex.nextLex();
					//check for constant here
					lex.nextLex();
				}else{
					throw new ParseException(20); // Throws the "expecting assignment arrow" exception
				}
			}else{
				throw new ParseException(27); // Expecting identifier
			}
		}else{
			throw new ParseException(6); // Expecting const (We probably don't need this one)
		}
		stop("constantDeclaration");
	}
	
	private void classDeclaration()throws ParseException{
		start("classDeclaration");
		if(lex.match("class")){
			lex.nextLex();
			if(lex.isIdentifier()){
				lex.nextLex();
				classBody();
			}else{
				throw new ParseException(27); // Expecting identifier
			}
		}else{
			throw new ParseException(5); // Expecting class
		}
		stop("classDeclaration");
	}
	
	private void classBody()throws ParseException{
		start("classBody");
		if(lex.match("begin")){
			lex.nextLex();
			while(!lex.match("end")){
				nonClassDeclaration();
				if(lex.match(";")){
					lex.nextLex();
				}else{
					throw new ParseException(18); // Expecting semicolon
				}
			}
		}else{
			throw new ParseException(4); // Expecting begin
		}
		stop("classBody");
	}
	
	private void nonClassDeclaration()throws ParseException{
		start("nonClassDeclaration");
		if(lex.match("function")){
			functionDeclaration();
		}
		stop("nonClassDeclaration");
	} // This needs to check for var, type, and const as well, right?
	
	private void functionDeclaration()throws ParseException{
		start("functionDeclaration");
		if(lex.match("function")){
			lex.nextLex();
			if(lex.isIdentifier()){
				lex.nextLex();
				arguments();
				returnType();
				functionBody();
			}else{
				throw new ParseException(27); // Expecting identifier
			}
		}else{
			throw new ParseException(10); // Expecting keyword function
		}
		stop("functionDeclaration");
		
	}
	
	private void arguments()throws ParseException{
		start("arguments");
		//lex.nextLex();
		if(lex.match("(")){
			lex.nextLex();
			argumentList();
			if(lex.match(")")){
				lex.nextLex();
			}else{
				throw new ParseException(22); // Expecting right parenthesis
			}
				
		}else{
			throw new ParseException(21); // Expecting left parenthesis
		}
		stop("arguments");
	}
	
	private void argumentList()throws ParseException{
		start("argumentList");
		if(lex.match(")")){
			
		}else{
			nameDeclaration();
			while(lex.match(",")){
				lex.nextLex();
				nameDeclaration();
			}
		}
		stop("argumentList");
	}
	
	private void nameDeclaration()throws ParseException{
		start("nameDeclaration");
		if(lex.isIdentifier()){
			lex.nextLex();
			if(lex.match(":")){
				lex.nextLex();
				type();
			}else{
				throw new ParseException(19); // Expecting colon
			}
		}else{
			throw new ParseException(27); // Expecting identifier
		}
		stop("nameDeclaration");	
	}
	
	private void type()throws ParseException{
		start("type");
		if(lex.isIdentifier()){
			lex.nextLex();
		}else if(lex.match("^")){
			lex.nextLex();
			type();
		}else if(lex.match("[")){
			lex.nextLex();
			if(lex.tokenCategory() == lex.intToken){
				lex.nextLex();
				if(lex.match(":")){
					lex.nextLex();
					if(lex.tokenCategory() == lex.intToken){
						lex.nextLex();
						//type();
					}else{
						throw new ParseException(32); // Expecting integer constant
					}
				}else{
					throw new ParseException(19); // Expecting colon
				}
			}else{
				throw new ParseException(32); // Expecting integer constant
			}
		}
		stop("type");	
	}
	
	private void returnType()throws ParseException{
		start("returnType");
		if(lex.match(":")){
			lex.nextLex();
			type();
		}
		stop("returnType");	
	}
	
	private void functionBody()throws ParseException{
		start("functionBody");
		if(lex.match("begin")){
			compoundStatement();
			//lex.nextLex();
		}else{
			nonClassDeclaration();
			if(lex.match(";")){
				lex.nextLex();
				functionBody();
			}else{
				throw new ParseException(18); // Expecting semicolon
			}
		}
		stop("functionBody");	
	}
	
	private void compoundStatement()throws ParseException{
		start("compoundStatement");
		if(lex.match("begin")){
			lex.nextLex();
			while(!lex.match("end")){
				statement();
				if(!lex.match(";")){
					throw new ParseException(18); // Expecting semicolon
				}else{
					lex.nextLex();
				}
			}
			lex.nextLex();
		}else{
			throw new ParseException(4); // Expecting keyword begin
		}
		stop("compoundStatement");	
	}
	
	private void statement()throws ParseException{
		start("statement");
		if(lex.match("return")){
			returnStatement();
		}else if(lex.isIdentifier()){
			assignOrFunction();
		}else if(lex.match("if")){
			ifStatement();
		}else if(lex.match("while")){
			whileStatement();
		}else if(lex.match("begin")){
			compoundStatement();
		}else{
			//throw exception, not sure what type
			throw new ParseException(0);
		}
		stop("statement");	
	}
	
	private void ifStatement()throws ParseException{
		start("ifStatement");
		if(lex.match("if")){
			lex.nextLex();
			if(lex.match("(")){
				lex.nextLex();
				expression();
				//lex.nextLex();
				
				if(lex.match(")")){
					lex.nextLex();
					statement();
					if(lex.match("else")){
						lex.nextLex();
						statement();
					}
				}else{
					System.out.println("Throwing up in ifStatement for matching )\n");
					throw new ParseException(22); // Expecting right parenthesis			
				}
			}else{
				throw new ParseException(21); // Expecting left parenthesis				
			}
		}else{
			throw new ParseException(11); // Expecting keyword if
		}
		stop("ifStatement");	
	}
	
	private void whileStatement()throws ParseException{
		start("whileStatement");
		if(lex.match("while")){
			lex.nextLex();
			if(lex.match("(")){
				lex.nextLex();
				expression();
				if(lex.match(")")){
					lex.nextLex();
					statement();
				}else{
					System.out.println("whileStatement expecting )");
					throw new ParseException(22); // Expecting right parenthesis
				}
			}else{
				System.out.println("whileStatement expecting (");
				throw new ParseException(21); // Expecting left parenthesis
			}
		}else{
			throw new ParseException(16); // Expecting keyword while
		}
		stop("whileStatement");	
	}
	
	private void returnStatement()throws ParseException{
		start("returnStatement");
		
		stop("returnStatement");	
	}
	
	private void assignOrFunction()throws ParseException{
		start("assignOrFunction");
		if(lex.isIdentifier()){
			reference();
			//lex.nextLex();
			if(lex.match("=")){
				lex.nextLex();
				expression();
			}else if(lex.match(("("))){
				lex.nextLex();
				parameterList();
				//lex.nextLex();
				if(lex.match(")")){
					lex.nextLex();
				}else{
					throw new ParseException(22); // Expecting right parenthesis
				}
			}else{
				throw new ParseException(21); // Expecting left parenthesis
			}
		}else{
			throw new ParseException(27); // Expecting identifier
		}
		stop("assignOrFunction");	
	}
	
	private void reference()throws ParseException{
		start("reference");
		if(lex.isIdentifier()){
			lex.nextLex();
			
			if(lex.match("^")){
				lex.nextLex();
			}else if(lex.match(".")){
				lex.nextLex();
				if(lex.isIdentifier()){
					lex.nextLex();
				}else{
					throw new ParseException(27); // Expecting identifier
				}
			}else if(lex.match("[")){
				lex.nextLex();
				expression();
				if(lex.match("]")){
					lex.nextLex();
				}else{
					throw new ParseException(24); // Expecting right bracket
				}
			}
		}else{
			throw new ParseException(27); // Expecting identifier
		}
		stop("reference");	
	}
	
	private void parameterList()throws ParseException{
		start("parameterList");
		if(!lex.match(")")){
			expression();
			//lex.nextLex();
			while(lex.match(",")){
				lex.nextLex();
				expression();
				//lex.nextLex();
			}
		}
		stop("parameterList");	
	}
	
	private void expression()throws ParseException{
		start("expression");
		relExpression();
		while((lex.match("or"))||(lex.match("and"))){
			lex.nextLex();
			relExpression();
		}
		stop("expression");	
	}
	
	private void relExpression()throws ParseException{
		start("relExpression");
		plusExpression();
		if((lex.match("<"))||(lex.match("<="))||(lex.match("!="))||(lex.match("=="))||(lex.match(">="))||(lex.match(">"))){
			lex.nextLex();
			plusExpression();
		}
		stop("relExpression");	
	}
	
	private void plusExpression()throws ParseException{
		start("plusExpression");
		timesExpression();
		while((lex.match("+"))||(lex.match("-"))||(lex.match("<<"))){
			lex.nextLex();
			timesExpression();
			
		}
		stop("plusExpression");	
	}
	
	private void timesExpression()throws ParseException{
		start("timesExpression");
		term();
		while((lex.match("*"))||(lex.match("/"))||(lex.match("%"))){
			if((lex.match("*"))||(lex.match("/"))){
				lex.nextLex();
				term();
			}else if((lex.match("%"))){
				//this is listed as taking two arguments, but not sure on exact syntax
			}
		}
		
		stop("timesExpression");	
	}
	
	private void term()throws ParseException{
		start("term");
		//lex.nextLex();
		if(lex.match("(")){
			lex.nextLex();
			expression();
			if(lex.match(")")){
				lex.nextLex();
			}else{
				throw new ParseException(22); // Expecting right parenthesis
			}
		}else if(lex.match("not")){
			lex.nextLex();
			term();
			//lex.nextLex();
		}else if(lex.match("new")){
			lex.nextLex();
			type();
			//lex.nextLex();
		}else if(lex.match("-")){
			lex.nextLex();
			term();
			//lex.nextLex();
		}else if(lex.isIdentifier()){
			reference();
			//lex.nextLex();
			if(lex.match("(")){
				lex.nextLex();
				parameterList();
				
				if(lex.match(")")){
					lex.nextLex();
				}else{
					throw new ParseException(22); // Expecting right parenthesis
				}
			}
		}else if(lex.match("&")){
			lex.nextLex();
			reference();
		}else if(lex.match("const")){
			constantDeclaration();
			
		}else if((lex.tokenCategory()==lex.stringToken)||(lex.tokenCategory()==lex.intToken)||(lex.tokenCategory()==lex.realToken)){
			lex.nextLex();
		}
		stop("term");	
	}
	
}
