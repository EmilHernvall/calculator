package se.c0la.calc;

public class Lexeme
{
	private TokenType type;
	private String value;
	
	public Lexeme(TokenType type)
	{
		this.type = type;
		this.value = null;
	}

	public Lexeme(TokenType type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	public TokenType getType() { return type; }
	public String getValue() { return value; }
}
