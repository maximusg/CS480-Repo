//
//		Compiler for CS 480
//	class Lexer
//
//		Written by Tim Budd, Winter term 2006
//
//		modified by: 
//

import java.io.*;

//
//--------Lexer----------------
//

public class Lexer {
	private PushbackReader input;
	private String token;
	private int tokenType;

	public Lexer(Reader in) {
		input = new PushbackReader(in);
	}

	private void skipWhiteSpace() throws ParseException, IOException {
		int c = input.read();
		while ((c != -1) && Character.isWhitespace((char) c))
			c = input.read();
		if (c == -1)
			return;
		if (c == '{') {
			c = input.read();
			while (c != '}') {
				if (c == '{')
					throw new ParseException(1);
				if (c == -1)
					throw new ParseException(1);
				c = input.read();
			}
			skipWhiteSpace();
		}
		else if (c != -1)
			input.unread(c);
	}
	
	public void nextLex() throws ParseException {
		token = "";
		try{
		skipWhiteSpace();
		int c = input.read();
		if (c == -1) {
			token = "<eof>";
			tokenType = endOfInput;
			return;
			}
		if (Character.isDigit((char) c)) {
			tokenType = intToken;
			while (Character.isDigit((char) c)) {
				token = token + (char) c;
				c = input.read();
				}
			if (c == '.') {
				tokenType = realToken;
				token = token + (char) c;
				c = input.read();
				while (Character.isDigit((char) c)) {
					token = token + (char) c;
					c = input.read();
					}
				}
			if (c != -1)
				input.unread(c);
			}
		else if (Character.isLetter((char) c)) {
			tokenType = identifierToken;
			while (Character.isLetterOrDigit((char) c)) {
				token = token + (char) c;
				c = input.read();
				}
			if (c != -1)
				input.unread(c);
			String [] keywords = {"begin", "class", "const", 
			    "and", "or",
			   "else", "end", "function", "if", "not", 
			   "return", "type", "while", "var", };
			for (int i = 0; i < keywords.length; i++)
		   		if (token.equals(keywords[i]))
					tokenType = keywordToken;
			}
		else if (c == '"') {
			tokenType = stringToken;
			c = input.read();
			while (c != '"') {
				token = token + (char) c;
				c = input.read();
				if (c == -1)
					throw new ParseException(2);
				}
			}
		else {
			tokenType = otherToken;
			token = token + (char) c;
			int d = input.read();
			if ((c == '<') && (d == '='))
					token = token + (char) d;
			else if ((c == '<') && (d == '<'))
					token = token + (char) d;
			else if ((c == '>') && (d == '='))
					token = token + (char) d;
			else if ((c == '=') && (d == '='))
					token = token + (char) d;
			else if ((c == '!') && (d == '='))
					token = token + (char) d;
			else if (d != -1)
				input.unread(d);
			}
		}
			catch (IOException e)
				{ throw new ParseException(0); }
	}

	static final int identifierToken = 1;
	static final int keywordToken = 2;
	static final int intToken = 3;
	static final int realToken = 4;
	static final int stringToken = 5;
	static final int otherToken = 6;
	static final int endOfInput = 7;

	public String tokenText() {
		return token;
	}

	public int tokenCategory() {
		return tokenType;
	}

	public boolean isIdentifier() {
		return tokenType == identifierToken;
	}

	public boolean match (String test) {
		return test.equals(token);
	}
}
