package parser;

import enums.script.Operator;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * An expression can be one of three things:
 * 1. a value. the endpoint of an expression stack
 * 2. a left-right expression with two further-leading expressions and an combining operator.
 * 3. an array of expressions which may go down further.
 *
 * Created by Michael on 11.09.2017.
 */
public class Expression {

	private Value value = null;

	private Expression left = null, right = null;
	private Operator operator;

	private ConcurrentLinkedDeque<Expression> array = null;

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Evaluation #######################################
	// ###################################################################################

	/**
	 * Returns the value of the expression. May be null.
	 */
	public Value getValue() {
		if (value != null) {
			return value;
		} else {
			return calculate();
		}
	}

	/**
	 * Calculates the left and right values with the operator.
	 */
	private Value calculate() {
		return null;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public void setValue(Value value) {
		this.value = value;
	}

	public Expression getLeft() {
		return left;
	}
	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}
	public void setRight(Expression right) {
		this.right = right;
	}

	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public ConcurrentLinkedDeque<Expression> getArray() {
		return array;
	}
	public void setArray(ConcurrentLinkedDeque<Expression> array) {
		this.array = array;
	}

	public String toString() {
		if (value != null) {
			return value.toString();
		} else {
			if ((left!=null)&&(right!=null)&&(operator!=null)) {
				return "<"+left+operator+right+">";
			} else {
				if (array!=null) {
					String str = "<";
					for (Expression exp : array) {
						str+=exp+",";
					}
					str += ">";
					return str;
				} else {
					return "empty";
				}
			}
		}
	}
}
