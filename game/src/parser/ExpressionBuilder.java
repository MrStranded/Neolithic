package parser;

import enums.script.Command;
import enums.script.DataType;
import enums.script.Operator;
import enums.script.Sign;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Michael on 11.09.2017.
 */
public class ExpressionBuilder {

	private static String[] operators = getOperatorArray();

	/**
	 * Entry to split a piece of code into a construct of expressions.
	 */
	public static Expression parseLine (String code) {
		Expression expression = new Expression();

		splitCode(expression,code);

		return expression;
	}

	/**
	 * Looks for separators in the code, splits it into expressions, expression arrays and operators and adds them accordingly.
	 * When nothing is found, then the code is interpreted as a value and added to the parent expression.
	 */
	private static void splitCode (Expression parent, String code) {
		System.out.println("splitCode: "+code);
		code = code.trim();

		boolean inString = false;

		int pos = 0;
		int l = code.length();

		while (pos < l) {
			char currentChar = code.charAt(pos);

			if (currentChar == Sign.QUOTATION_MARK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% string starts
				inString = !inString;
			}

			if (!inString) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% not in string

				Operator operator = getExistingOperator(code,pos);
				if (operator!=null) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% operator found! -> split
					Expression left = new Expression();
					Expression right = new Expression();

					parent.setLeft(left);
					parent.setRight(right);
					parent.setOperator(operator);

					splitCode(left,code.substring(0,pos));
					splitCode(right,code.substring(pos+operator.toString().length(),code.length()));
					return;

//				} else if (currentChar == Sign.OPEN_EXPRESSION.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% separate expression
//					int expressionEnd = findExpressionEnd(code,pos,Sign.OPEN_EXPRESSION.getChar(),Sign.CLOSE_EXPRESSION.getChar());
//
//					if (expressionEnd>=0) {
//						splitCode(parent,code.substring(pos+1,expressionEnd));
//						return;
//					}

				} else if (currentChar == Sign.OPEN_ARRAY.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% start of an array
					int expressionEnd = findExpressionEnd(code,pos,Sign.OPEN_ARRAY.getChar(),Sign.CLOSE_ARRAY.getChar());

					if (expressionEnd>=0) {
						splitCodeIntoArray(parent,code.substring(pos+1,expressionEnd));
						return;
					}
				}
			}

			pos++;
		}

		// When code did not exit method yet, then we deal with a raw value.
		parent.setValue(getValueFromCode(code));
	}

	/**
	 *
	 */
	private static Value getValueFromCode(String code) {
		if (code==null) return null;

		// ------- check for commands
		for (Command command : Command.values()) {
			//if ()
		}

		return new Value(DataType.TEXT,code);
		//return null;
	}

	/**
	 * This method takes a junk of code, splits it into expressions and adds them as a list to the parent.
	 */
	public static void splitCodeIntoArray(Expression parent, String code) {
		System.out.println("splitCodeIntoArray: "+code);
		ConcurrentLinkedDeque<Expression> expressions = new ConcurrentLinkedDeque<>();

		int expBracketLevel = 0;
		int arrBracketLevel = 0;
		boolean inString = false;

		int lastExp = 0;

		char[] scope = code.toCharArray();
		for (int i=0; i<scope.length; i++) {
			char c = scope[i];

			if (c == Sign.QUOTATION_MARK.getChar()) {
				inString = !inString;
			} else if (c == Sign.OPEN_ARRAY.getChar()) {
				arrBracketLevel++;
			} else if (c == Sign.OPEN_EXPRESSION.getChar()) {
				expBracketLevel++;
			} else if (c == Sign.CLOSE_ARRAY.getChar()) {
				arrBracketLevel--;
			} else if (c == Sign.CLOSE_EXPRESSION.getChar()) {
				expBracketLevel--;
			} else if (c == Sign.COMMA.getChar()) {

				if ((arrBracketLevel==0)&&(expBracketLevel==0)&&(!inString)) {
					Expression expression = new Expression();
					expressions.add(expression);

					splitCode(expression,code.substring(lastExp,i));

					lastExp = i+1;
				}
			}
		}

		Expression expression = new Expression();
		expressions.add(expression);
		parent.setArray(expressions);

		splitCode(expression,code.substring(lastExp,code.length()));
	}

	/**
	 * Returns the point in the string (code) where the close-brackets (ec) balance the open-brackets (sc).
	 * Returns -1 if no such point is found.
	 */
	private static int findExpressionEnd(String code, int i, char sc, char ec) {
		char[] scope = code.toCharArray();
		int level = 0;
		boolean inString = false;

		for (int j=i; j<scope.length; j++) {
			char c = scope[j];

			if (c == Sign.QUOTATION_MARK.getChar()) {
				inString = !inString;
			}

			if (!inString) {
				if (c == sc) {
					level++;
				} else if (c == ec) {
					level--;
					if (level == 0) return j;
				}
			}
		}

		return -1;
	}

	/**
	 * Looks for the longest fitting substring that fits an operator from the operator enum and returns it.
	 */
	private static Operator getExistingOperator (String code, int i) {
		Operator op = null;
		for (int j=0; j<operators.length; j++) {
			String operator = operators[j];
			if (code.length() >= i+operator.length()) {
				if (code.substring(i,i+operator.length()).equals(operator)) {
					if ((op == null)||(operator.length() > op.toString().length())) {
						op = Operator.values()[j];
					}
				}
			}
		}
		return op;
	}

	/**
	 * The values of the Operator Enum as String array.
	 */
	private static String[] getOperatorArray() {
		Operator[] operatorsAsEnums = Operator.values();
		String[] operatorsAsStrings = new String[operatorsAsEnums.length];
		for (int i=0; i<operatorsAsEnums.length; i++) {
			operatorsAsStrings[i] = operatorsAsEnums[i].toString();
		}
		return operatorsAsStrings;
	}

}
