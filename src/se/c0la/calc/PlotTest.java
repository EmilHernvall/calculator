package se.c0la.calc;

import java.util.*;
import java.math.*;

public class PlotTest
{
	public static void main(String[] args)
	throws Exception
	{
		Lexer lexer = new Lexer();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		FunctionPlotter plotter = new FunctionPlotter(evaluator);
		
		/*String testFunction = "2*sin(pi*x)+1";
		plotter.setImageSize(800, 400);
		plotter.setBoundsX(-4.0, 4.0);
		plotter.setBoundsY(-1.5, 3.5);*/
		
		String testFunction = "(x-1)*(x+1)";
		plotter.setImageSize(800, 600);
		plotter.setBoundsX(-4.0, 4.0);
		plotter.setBoundsY(-1.5, 9.0);
		
		ASTNode expression = parser.parse(lexer.tokenize(testFunction));
		plotter.plot(expression, "x", "test.png");
	}
}
