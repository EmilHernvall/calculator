package se.c0la.calc;

import java.util.*;

/**
 * Transform a sequence of lexemes into an abstract syntax tree
 * that can be easily evaluated.
 */
public class Parser
{
	/**
	 * Used in the intermediate parsing steps when part
	 * of the sequence is AST nodes, and part of the sequence
	 * is lexemes.
	 */
	private static class TokenWrapper
	{
		private ASTNode node;
		private Lexeme lexeme;
		
		public TokenWrapper(ASTNode node)
		{
			this.node = node;
			this.lexeme = null;
		}
		
		public TokenWrapper(Lexeme lexeme)
		{
			this.lexeme = lexeme;
			this.node = null;
		}
		
		public Lexeme getLexeme() { return lexeme; }
		
		public ASTNode getNode() 
		{ 
			if (node != null) {
				return node; 
			}
			
			return new ASTNode(lexeme);
		}
		
		public boolean isLexeme() { return node == null; }
	}
	
	private boolean debug;

	public Parser()
	{
		this(false);
	}
	
	public Parser(boolean debug)
	{
		this.debug = debug;
	}
	
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * Call this to transform the lexemes into an AST.
	 */	
	public ASTNode parse(List<Lexeme> lexemes)
	throws ParseErrorException
	{
		// This method is called recusively, so it's useful
		// when debugging to output the list of lexemes being
		// analyzed.
		if (debug) {
			for (Lexeme lexeme : lexemes) {
				if (lexeme.getType() == TokenType.NUMBER) {
					System.out.println("NUMBER: " + lexeme.getValue());
				} else {
					System.out.println(lexeme.getType());
				}
			}
			
			System.out.println();
		}
		
		List<TokenWrapper> tokenList = new ArrayList<TokenWrapper>();
		
		// Eliminate all paranthesis and functions.
		// Call self recursively for all sub-expressions.
		int depth = 0, pos = -1;
		List<Lexeme> subList = null;
		for (int i = 0; i < lexemes.size(); i++) {
			Lexeme lexeme = lexemes.get(i);
			switch (lexeme.getType()) {

				// When an opening paranthesis is detected, we just store
				// the location and wait patiently until we find the
				// corresponding closing paranthesis.
				case OPEN_PARANTHESIS:
				case FUNCTION:
					depth++;
					if (depth == 1) {
						pos = i;
					}
					break;

				// When a closing paranthesis is found we call ourselves
				// again with the list of lexemes contained within
				// the current paranthesis. We then insert the partial
				// AST returned into the token list.
				case CLOSE_PARANTHESIS:
					if (depth == 1) {
						subList = lexemes.subList(pos + 1, i);
						ASTNode subAST = parse(subList);
						
						// If this is a function rather than an regular
						// pair of paranthesis, we wrap it in another AST node.
						Lexeme openLexeme = lexemes.get(pos);
						if (openLexeme.getType() == TokenType.FUNCTION) {
							subAST = new ASTNode(openLexeme, subAST, null);
						}
						
						tokenList.add(new TokenWrapper(subAST));
					}
					else if (depth == 0) {
						throw new ParseErrorException("Unmatched paranthesis");
					}
					
					depth--;
					break;

				// Any lexemes that are not contained within a pair of paranthesis
				// are added directly to the token list, without being transformed
				// into AST nodes. This occures later.
				default:
					if (depth == 0) {
						tokenList.add(new TokenWrapper(lexeme));
					}
			}
		}
		
		// Make sure that we're back at the base depth.
		if (depth != 0) {
			throw new ParseErrorException("Unmatched paranthesis");
		}
		
		// Handle operators in order of precedence
		tokenList = consumeUnaryOperators(tokenList, EnumSet.of(TokenType.MINUS));
		
		tokenList = consumeBinaryOperators(tokenList, EnumSet.of(TokenType.EXPONENT));
		tokenList = consumeBinaryOperators(tokenList, EnumSet.of(TokenType.ASTERIX, TokenType.SLASH));
		tokenList = consumeBinaryOperators(tokenList, EnumSet.of(TokenType.PLUS, TokenType.MINUS));
		
		if (tokenList.size() != 1) {
			throw new ParseErrorException("Something went horribly wrong along the way.");
		}
		
		TokenWrapper wrapper = tokenList.get(0);
		return wrapper.getNode();
	}
	
	private List<TokenWrapper> consumeUnaryOperators(List<TokenWrapper> tokens, Set<TokenType> types)
	throws ParseErrorException
	{
		List<TokenWrapper> result = new ArrayList<TokenWrapper>();
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenWrapper wrapper = tokens.get(i);
			
			if (!wrapper.isLexeme()) {
				result.add(wrapper);
				continue;
			}
			
			Lexeme lexeme = wrapper.getLexeme();
			
			if (!types.contains(lexeme.getType())) {
				result.add(new TokenWrapper(lexeme));
				continue;
			}
			
			if (i > 0) {
				TokenWrapper prev = tokens.get(i-1);
				
				if (prev.isLexeme()) {
					Lexeme prevLexeme = prev.getLexeme();
					if (prevLexeme.getType() == TokenType.NUMBER ||
						prevLexeme.getType() == TokenType.VARIABLE) {
						
						result.add(new TokenWrapper(lexeme));
						continue;
					}
				}
			}
			
			try {
				TokenWrapper next = tokens.get(i+1);
				
				if (next.isLexeme()) {
					Lexeme nextLexeme = next.getLexeme();
					if (nextLexeme.getType() != TokenType.NUMBER &&
						nextLexeme.getType() != TokenType.VARIABLE) {
						throw new ParseErrorException("Expected number or variable");
					}
				}
				
				ASTNode node = new ASTNode(lexeme, next.getNode(), null);
				result.add(new TokenWrapper(node));
			}
			catch (IndexOutOfBoundsException e) {
				throw new ParseErrorException("Operator without argument.");
			}
			
			i++;
		}
		
		return result;
	}
	
	/**
	 * This method is used to consume the specified operators from the list lexemes and
	 * ast nodes. This is only done after all paranthesis have been removed as replaced
	 * with AST nodes. By calling this method repeatedly according to the precedence order
	 * of the operators, we can generate an correct AST.
	 */
	private List<TokenWrapper> consumeBinaryOperators(List<TokenWrapper> tokens, Set<TokenType> types)
	throws ParseErrorException
	{
		List<TokenWrapper> result = new ArrayList<TokenWrapper>();

		// Used when we're chaining together nodes.
		ASTNode node = null;
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenWrapper wrapper = tokens.get(i);

			// If the current token already happens to be an ASTNode
			// we just add it to the result list and continue.
			if (!wrapper.isLexeme()) {
				result.add(wrapper);
				continue;
			}
			
			Lexeme lexeme = wrapper.getLexeme();
			
			// If this is an lexeme of an type that isn't being analyzed
			// in this invocation, we add it to the result without further
			// processing.
			if (!types.contains(lexeme.getType())) {

				// When a lexeme that isn't interesting is found,
				// we know that we won't have to chain any more nodes
				// together. 
				if (node != null) {
					result.add(new TokenWrapper(node));
					node = null;
				}
				
				result.add(new TokenWrapper(lexeme));
				continue;
			}

			// If there is a node that hasn't been added to the result,
			// it means that it will have to be chained together with
			// the token after the current one. Thus we create a new 
			// ASTNode, with the current lexeme specified as the operation.
			if (node != null) {
				TokenWrapper next = tokens.get(i+1);
				node = new ASTNode(lexeme, node, next.getNode());
				i++;
				continue;
			}

			try {
				// We're creating a completely new node now, which means that since
				// all operators are binary we have to consume both the previous
				// and the next token. The previous will already have been added to
				// the list, so we remove it again.
				result.remove(result.size()-1);
			
				TokenWrapper prev = tokens.get(i-1);
				if (prev.isLexeme()) {
					Lexeme prevLexeme = prev.getLexeme();
					if (prevLexeme.getType() != TokenType.NUMBER &&
						prevLexeme.getType() != TokenType.VARIABLE) {
						throw new ParseErrorException("Expected number or variable");
					}
				}
				
				TokenWrapper next = tokens.get(i+1);
				
				if (next.isLexeme()) {
					Lexeme nextLexeme = next.getLexeme();
					if (nextLexeme.getType() != TokenType.NUMBER &&
						nextLexeme.getType() != TokenType.VARIABLE) {
						throw new ParseErrorException("Expected number or variable");
					}
				}
				
				node = new ASTNode(lexeme, prev.getNode(), next.getNode());
			}
			catch (IndexOutOfBoundsException e) {
				throw new ParseErrorException("Operator without argument.");
			}
			
			i++;
		}
		
		// Check if there's one last node to add.
		if (node != null) {
			result.add(new TokenWrapper(node));
		}
		
		return result;
	}
}
