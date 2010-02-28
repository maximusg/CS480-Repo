//
//	CS 480/580
//		Driver for programming assignment 5
//		Written by Tim Budd, Winter Term 1998
//

import java.io.*;

class Asgn5 {
	public static void main(String [ ] args) {
		System.out.println("Reading file " + args[0]);
		try {
			FileReader instream = new FileReader(args[0]);
			Parser par = new Parser(new Lexer(instream), false);
			par.parse();
		}
			catch(ParseException e) 
				{ System.out.println("Parse Error " + e); }
			catch(FileNotFoundException e) 
				{ System.err.println("File not found " + e); }
			catch(IOException e) 
				{ System.err.println("File IO Exception " + e); }
		}
}
