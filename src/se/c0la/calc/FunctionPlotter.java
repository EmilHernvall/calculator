package se.c0la.calc;

import java.util.*;
import java.math.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

public class FunctionPlotter
{
	private Evaluator evaluator;
	
	private int imageWidth, imageHeight;
	
	private double lowerBoundX, upperBoundX;
	private double lowerBoundY, upperBoundY;
	
	private double scaleX, scaleY;

	public FunctionPlotter(Evaluator evaluator)
	{
		this.evaluator = evaluator;
		
		this.imageWidth = 100;
		this.imageHeight = 100;
		
		this.lowerBoundX = 0.0;
		this.upperBoundX = 0.0;
		this.lowerBoundY = 0.0;
		this.upperBoundY = 0.0;
		
		this.scaleX = 1.0;
		this.scaleY = 1.0;
	}
	
	public void setImageSize(int width, int height)
	{
		this.imageWidth = width;
		this.imageHeight = height;
	}
	
	public void setBoundsX(double lowerBound, double upperBound)
	{
		this.lowerBoundX = lowerBound;
		this.upperBoundX = upperBound;
	}
	
	public void setBoundsY(double lowerBound, double upperBound)
	{
		this.lowerBoundY = lowerBound;
		this.upperBoundY = upperBound;
	}
	
	public void setScale(double scaleX, double scaleY)
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	private int getCoordinateX(double x)
	{
		return (int)(imageWidth * (x - lowerBoundX) / (upperBoundX - lowerBoundX));
	}
	
	private int getCoordinateY(double y)
	{
		return (int)(imageHeight - imageHeight * (y - lowerBoundY) / (upperBoundY - lowerBoundY));
	}
	
	public BufferedImage plot(ASTNode expression, String var)
	throws Exception
	{
		double step = (upperBoundX - lowerBoundX) / (imageWidth);
		
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setBackground(new Color(255, 255, 255));
		g.clearRect(0, 0, imageWidth, imageHeight);
		g.setColor(new Color(0, 0, 0));
		g.setStroke(new BasicStroke(2.0f));
		
		int origoX = getCoordinateX(0.0);
		int origoY = getCoordinateY(0.0);
		
		System.out.println(origoX + " " + origoY);
		
		g.drawLine(0, origoY, imageWidth, origoY);
		g.drawLine(origoX, 0, origoX, imageHeight);
		
		g.setStroke(new BasicStroke(1.0f));
		
		for (double xMarker = Math.floor(lowerBoundX); xMarker < upperBoundX; xMarker += scaleX) {
			int xMarkerPos = getCoordinateX(xMarker);
			g.setColor(new Color(200, 200, 200));
			g.drawLine(xMarkerPos, 0, xMarkerPos, imageHeight);
			g.setColor(new Color(0, 0, 0));
			g.drawLine(xMarkerPos, origoY - 10, xMarkerPos, origoY + 10);
			g.drawString(String.format("%.2f", xMarker), xMarkerPos + 5, origoY + 25);
		}
		
		for (double yMarker = Math.floor(lowerBoundX); yMarker < upperBoundY; yMarker += scaleY) {
			int yMarkerPos = getCoordinateY(yMarker);
			g.setColor(new Color(200, 200, 200));
			g.drawLine(0, yMarkerPos, imageWidth, yMarkerPos);
			g.setColor(new Color(0, 0, 0));
			g.drawLine(origoX - 10, yMarkerPos, origoX + 10, yMarkerPos);
			g.drawString(String.format("%.2f", yMarker), origoX + 20, yMarkerPos - 5);
		}
		
		g.setColor(new Color(100, 100, 255));
		
		double x = lowerBoundX;
		int prevX = -1, prevY = -1;
		while (x < upperBoundX) {
			evaluator.addVariable(var, new BigDecimal(x));
			
			double y;
			try {
				y = evaluator.evaluate(expression).doubleValue();
			}
			catch (EvaluationException e) {
				x += step;
				continue;
			}
			
			int xPos = getCoordinateX(x);
			int yPos = getCoordinateY(y);
			
			//System.out.println(xPos + " " + yPos);
			
			if (prevX != -1 && prevY != -1) {
				g.drawLine(prevX, prevY, xPos, yPos);
			}
			
			prevX = xPos;
			prevY = yPos;
			x += step;
		}
		
		return image;
	}
}

