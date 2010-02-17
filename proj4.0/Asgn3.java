//
//	CS 480/580
//		Driver for programming assignment 3
//		Written by Tim Budd, Winter Term 1998
//

import java.io.*;

class Asgn3 {
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
				{ System.out.println("File not found " + e); }
			catch(IOException e) 
				{ System.out.println("File IO Exception " + e); }
		}
}
