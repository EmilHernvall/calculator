package se.c0la.calc;

public class UndefinedVariableException extends EvaluationException
{
	public UndefinedVariableException(String message)
	{
		super(message);
	}
}
