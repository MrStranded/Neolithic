package parser;

import enums.script.DataType;
import environment.world.Entity;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The class to actually execute scripts.
 *
 * Created by Michael on 12.09.2017.
 */
public class ScriptExecuter {

	/**
	 * Executes the scriptBlocks one by one and maybe executes sub scriptBlocks if their conditional expression is 1.
	 */
	public static void executeScriptBlocks(ConcurrentLinkedDeque<ScriptBlock> scriptBlocks, Entity parent) {

		for (ScriptBlock scriptBlock : scriptBlocks) {
			System.out.println("exec: "+scriptBlock.getExpression());
			if (scriptBlock.getScriptBlocks() == null) { // no more sub blocks -> execute
				executeScript(scriptBlock.getExpression(),parent);
			} else { // more sub blocks -> check conditional expression and maybe execute

			}
		}

	}

	/**
	 * Executes an expression.
	 */
	private static Value executeScript(Expression expression, Entity parent) {
		if ((expression.getLeft() == null) || (expression.getRight() == null)) { // only evaluate value
			return getValue(expression);
		} else {
			Value leftValue = executeScript(expression.getLeft(),parent);
			if (leftValue == null) return null;
			switch (expression.getOperator()) { // going through the operators
				case LET:
					break;
			}
		}
		return null;
	}

	private static Value getValue(Expression expression) {
		return expression.getValue();
	}

}
