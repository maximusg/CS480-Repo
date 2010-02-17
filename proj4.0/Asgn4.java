//
//	CS 480/580
//		Driver for programming assignment 4
//		Written by Tim Budd, Spring Term 2000
//

import java.io.*;

class Asgn4 {
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
