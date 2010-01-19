//
//	parser skeleton, CS 480, Winter 2006
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
			if(lex.match("function")){
				nonClassDeclaration();
			}else{
				throw new ParseException(0); //this is not the correct error to throw and needs to be changed
			}
		stop("declaration");
		}catch (ParseException e){
			
		}
	}

	private void constantDeclaration()throws ParseException{
		start("constantDeclaration");
		
		stop("constantDeclaration");
	}
	
	private void nonClassDeclaration()throws ParseException{
		start("nonClassDeclaration");
		if(lex.match("function")){
			functionDeclaration();
		}
		stop("nonClassDeclaration");
	}
	
	private void functionDeclaration()throws ParseException{
		start("arguments");
		if(lex.match("(")){
			
		}
		stop("arguments");
		
	}
	
	private void argumentList()throws ParseException{
		start("argumentList");
		
		stop("argumentList");
	}
	
	private void returnType()throws ParseException{
		start("returnType");
		
		stop("returnType");	
	}
	
	private void functionBody()throws ParseException{
		start("functionBody");
		
		stop("functionBody");	
	}
	
	private void compoundStatement()throws ParseException{
		start("compoundStatement");
		
		stop("compoundStatement");	
	}
	
	private void statement()throws ParseException{
		start("statement");
		
		stop("statement");	
	}
	
	private void assignOrFunction()throws ParseException{
		start("assignOrFunction");
		
		stop("assignOrFunction");	
	}
	
	private void reference()throws ParseException{
		start("reference");
		
		stop("reference");	
	}
	
	private void parameterList()throws ParseException{
		start("parameterList");
		
		stop("parameterList");	
	}
	
	private void expression()throws ParseException{
		start("expression");
		
		stop("expression");	
	}
	
	private void relExpression()throws ParseException{
		start("relExpression");
		
		stop("relExpression");	
	}
	
	private void plusExpression()throws ParseException{
		start("plusExpression");
		
		stop("plusExpression");	
	}
	
	private void timesExpression()throws ParseException{
		start("timesExpression");
		
		stop("timesExpression");	
	}
	
	private void term()throws ParseException{
		start("term");
		
		stop("term");	
	}
	
}
