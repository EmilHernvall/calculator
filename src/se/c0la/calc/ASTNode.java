package se.c0la.calc;

import java.util.*;

public class ASTNode
{
	private Lexeme lexeme;
	private ASTNode left;
	private ASTNode right;
	
	public ASTNode(Lexeme lexeme)
	{
		this.lexeme = lexeme;
		this.left = null;
		this.right = null;
	}

	public ASTNode(Lexeme lexeme, ASTNode left, ASTNode right)
	{
		this.lexeme = lexeme;
		this.left = left;
		this.right = right;
	}
	
	public ASTNode(Lexeme lexeme, Lexeme left, Lexeme right)
	{
		this.lexeme = lexeme;
		this.left = new ASTNode(left);
		this.right = new ASTNode(right);
	}

	public Lexeme getLexeme() { return lexeme; }
	public ASTNode getLeft() { return left; }
	public ASTNode getRight() { return right; }
}
