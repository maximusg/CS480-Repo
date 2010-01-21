//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by: Richard Tracy, Brad Kessler
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
				throw new ParseException(0); //this is not the correct error to throw and needs to be changed
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
					
				}else{
					throw new ParseException(0);
					//throw error saying that = was expected
				}
			}else{
				throw new ParseException(0);
				//throw error saying that identifier was expected
			}
		}else{
			throw new ParseException(0);
			//throw error that const was expected.  We probably don't need this one
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
				throw new ParseException(0);
				//throw error for expecting identifier
			}
		}else{
			throw new ParseException(0);
			//throw error for expecting class
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
					throw new ParseException(0);
					//throw error for expecting ;
				}
			}
		}else{
			throw new ParseException(0);
			//throw error expecting begin
		}
		stop("classBody");
	}
	
	private void nonClassDeclaration()throws ParseException{
		start("nonClassDeclaration");
		if(lex.match("function")){
			functionDeclaration();
		}else if((lex.match("var"))||(lex.match("const"))||(lex.match("type"))){
			nonFunctionDeclaration();
		}
		stop("nonClassDeclaration");
	}
	
	private void nonFunctionDeclaration()throws ParseException{
		start("nonfunctionDeclaration");
		if(lex.match("var")){
			variableDeclaration();
		}else if(lex.match("type")){
			typeDeclaration();
		}else if(lex.match("const")){
			constantDeclaration();
		}else{
			throw new ParseException(0);
			//throw some type of error
		}
		stop("nonfunctionDeclaration");
		
	}
	
	private void typeDeclaration()throws ParseException{
		start("typeDeclaration");
		if(lex.match("type")){
			lex.nextLex();
			nameDeclaration();
		}else{
			throw new ParseException(0);
			//throw error that we are expecting type
		}
		stop("typeDeclaration");
		
	}
	
	private void variableDeclaration()throws ParseException{
		start("variableDeclaration");
		if(lex.match("var")){
			lex.nextLex();
			nameDeclaration();
		}else{
			throw new ParseException(0);
			//throw error that we are expecting var
		}
		stop("variableDeclaration");
		
	}
	
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
				throw new ParseException(0);
				//throw some type of error
			}
		}else{
			throw new ParseException(0);
			//throw some type of error
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
				throw new ParseException(0);
				//throw an error that it was expecting )
			}
				
		}else{
			throw new ParseException(0);
			//throw an error that it was expecting (
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
				throw new ParseException(0);
				//throw error that it was expecting :
			}
		}else{
			throw new ParseException(0);
			//throw error that it is not an identifier
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
						throw new ParseException(0);
						//throw an error that it was expecting an integer token
					}
				}else{
					throw new ParseException(0);
					//throw an error that it was expecting a :
				}
			}else{
				throw new ParseException(0);
				//throw an error that it was expecting an integer token
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
		while(!lex.match("begin")){
			nonClassDeclaration();
			//lex.nextLex();	
			if(lex.match(";")){
				lex.nextLex();
			}else{
				System.out.println("Exception in functionBody");
				//throw error for expecting ;
				throw new ParseException(0);	
			}
		}
		compoundStatement();

		stop("functionBody");	
	}
	
	private void compoundStatement()throws ParseException{
		start("compoundStatement");
		if(lex.match("begin")){
			lex.nextLex();
			while(!lex.match("end")){
				statement();
				if(!lex.match(";")){
					throw new ParseException(0);
					//throw exception that we were expecting a  ;
				}else{
					lex.nextLex();
				}
			}
			lex.nextLex();
		}else{
			throw new ParseException(0);
			//throw error that begin was expected
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
					//throw exception for expecting )
					System.out.println("Throwing up in ifStatement for matching )\n");
					throw new ParseException(0);				
				}
			}else{
				//throw exception for expecting (
				throw new ParseException(0);				
			}
		}else{
			//throw exception for expecting if
			throw new ParseException(0);
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
					//throw exception for expecting )
					System.out.println("whileStatement expecting )");
					throw new ParseException(0);
				}
			}else{
				//throw error for expecting (
				System.out.println("whileStatement expecting (");
				throw new ParseException(0);
			}
		}else{
			//throw parse exception for expecting while
			throw new ParseException(0);
		}
		stop("whileStatement");	
	}
	
	private void returnStatement()throws ParseException{
		start("returnStatement");
		if(lex.match("return")){
			lex.nextLex();
			if(lex.match("(")){
				lex.nextLex();
				expression();
				//lex.nextLex();
				if(!lex.match(")")){
					System.out.println("In returnStatement expecting a )");
					throw new ParseException(0);
					//throw error that return was expected		
				}else{
					lex.nextLex();
				}
			}
		}else{
			throw new ParseException(0);
			//throw error that return was expected	
		}
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
					throw new ParseException(0);
					//throw error that ) was expected
				}
			}else{
				throw new ParseException(0);
				//throw error that = or ( was expected
			}
		}else{
			throw new ParseException(0);
			//throw error that identifier was expected
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
					throw new ParseException(0);
					//throw error that identifier was expected
				}
			}else if(lex.match("[")){
				lex.nextLex();
				expression();
				if(lex.match("]")){
					lex.nextLex();
				}else{
					throw new ParseException(0);
					//throw error that ] was expected
				}
			}
		}else{
			throw new ParseException(0);
			//throw error that identifier was expected
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
				throw new ParseException(0);
				//throw error that ) was expected
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
					throw new ParseException(0);
					//throw error that ) was expected
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
