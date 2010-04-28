package se.c0la.calc;

import java.util.*;
import java.math.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class PlotTest
{
	public static void main(String[] args)
	throws Exception
	{
		Lexer lexer = new Lexer();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		FunctionPlotter plotter = new FunctionPlotter(evaluator);
		
		long time = System.currentTimeMillis();
		
		/*String testFunction = "2*sin(pi*x)+1";
		plotter.setImageSize(800, 400);
		plotter.setBoundsX(-4.0, 4.0);
		plotter.setBoundsY(-1.5, 3.5);*/
		
		/*String testFunction = "(x-1)*(x+1)";
		plotter.setImageSize(800, 600);
		plotter.setBoundsX(-4.0, 4.0);
		plotter.setBoundsY(-1.5, 9.0);
		plotter.setScale(0.5, 1.0);*/
		
		/*String testFunction = "4*x*(x-1)*(x+1)";
		plotter.setImageSize(800, 600);
		plotter.setBoundsX(-1.25, 1.25);
		plotter.setBoundsY(-2.0, 2.0);
		plotter.setScale(0.25, 0.25);*/
		
		String testFunction = "1/2 * ln((1+sqrt(1-x^2))/(1-sqrt(1-x^2)))-sqrt(1-x^2)+sqrt(x/10000000)";
		plotter.setImageSize(600, 800);
		plotter.setBoundsX(-0.1, 1.0);
		plotter.setBoundsY(-0.5, 7.0);
		plotter.setScale(0.1, 1.0);
		
		ASTNode expression = parser.parse(lexer.tokenize(testFunction));
		BufferedImage image = plotter.plot(expression, "x");
		
		time = System.currentTimeMillis() - time;
		
		System.out.println("in " + time + " ms");
		
		ImageIO.write(image, "png", new File("test.png"));
	}
}
