package se.c0la.calc;

public class UndefinedFunctionException extends EvaluationException
{
	public UndefinedFunctionException(String message)
	{
		super(message);
	}
}
