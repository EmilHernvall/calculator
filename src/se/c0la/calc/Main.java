package se.c0la.calc;

import java.util.*;
import java.math.*;

public class Main
{
	public static void main(String[] args)
	throws Exception
	{
		//String str = "33 * (4477 - 2233 + 10) + ((44 - 32) / (27 + 32))";
		//String str = "2 * 3 * 4 - 5 * 7 + 1";
		//String str = "2^(4+4/2+2)-56*2/2";
		//String str = "2*sin(pi / 4)^2 + 2";
		//String str = "e^ln(7.0)";
		//String str = "2*foo+6.1";
	
		System.out.println("se.c0la.calc 0.1");
		System.out.println("type quit to exit. you can use debugon and debugoff to");
		System.out.println("to turn debugging on and off.");
		System.out.println("assign variables using \"name = expression\"");
		System.out.println();
		System.out.println("the following operators are supported: +-*/()^");
		System.out.println("these constants are included: pi, e");
		System.out.println("and these functions: sin(), cos(), tan(), ln(), log(), log10(), asin(), acos(), atan()");
		System.out.println();
		System.out.println("you can now enter an arbitrary math expression:");
	
		Lexer lexer = new Lexer();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("> ");
			
			String str;
			try {
				str = scanner.nextLine();
			}
			catch (Exception e) {
				System.out.println();
				break;
			}
			
			if (str.equals("quit")) {
				break;
			}
			else if (str.equals("debugon")) {
				parser.setDebug(true);
				evaluator.setDebug(true);
				System.out.println("debug mode is now enabled.");
				continue;
			}
			else if (str.equals("debugoff")) {
				parser.setDebug(false);
				evaluator.setDebug(false);
				System.out.println("debug mode is now disabled.");
				continue;
			}
			else if (str.startsWith("precision")) {
				String[] split = str.split(" ");
				try {
					int precision = Integer.parseInt(split[1]);
					evaluator.setPrecision(precision);

					System.out.println("precision set to " + precision);
				}
				catch (Exception e) {}
				continue;
			}
		
			try {
				long parseTime = System.currentTimeMillis();

				List<Lexeme> lexemes = lexer.tokenize(str);
				
				String variable = null;
				if (lexemes.size() > 2) {
					Lexeme first = lexemes.get(0);
					Lexeme second = lexemes.get(1);
					
					if (first.getType() == TokenType.VARIABLE && 
						second.getType() == TokenType.EQUALSIGN) {
					
						variable = first.getValue();
						lexemes.remove(0);
						lexemes.remove(0);
					}
				}
				
				ASTNode ast = parser.parse(lexemes);

				parseTime = System.currentTimeMillis() - parseTime;
				
				long evalTime = System.currentTimeMillis();
				BigDecimal value = evaluator.evaluate(ast);
				evalTime = System.currentTimeMillis() - evalTime;
				
				if (variable != null) {
					evaluator.addVariable(variable, value);
				}
				
				System.out.println(value);
				System.out.println("parsing: " + parseTime + " ms, eval: " + evalTime + " ms");
			}
			catch (UnknownTokenException e) {
				System.out.println("Unknown token: " + e.getMessage());
			}
			catch (ParseErrorException e) {
				System.out.println("Parse error: " + e.getMessage());
			}
			catch (EvaluationException e) {
				System.out.println("Evaluation failed: " + e.getMessage());
			}
		}
	}
}
