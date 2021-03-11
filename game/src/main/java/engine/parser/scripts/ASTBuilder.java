package engine.parser.scripts;

import engine.data.scripts.Script;
import engine.parser.utils.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.interpretation.Interpreter;
import engine.parser.scripts.nodes.*;
import engine.parser.tokenization.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder {

	private Interpreter interpreter;

	public ASTBuilder(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	// ###################################################################################
	// ################################ Entry Point ######################################
	// ###################################################################################

	public Script buildScript(String textID) throws Exception {
		Token next = interpreter.peek();
		List<String> parameters = new ArrayList<>(0);

		if (TokenConstants.ROUND_BRACKETS_OPEN.equals(next)) { // we're gonna read in some parameters
			interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);
			boolean firstParameter = true;

			while (!TokenConstants.ROUND_BRACKETS_CLOSE.equals(next = interpreter.peek())) {
				if (!firstParameter) {
					interpreter.consume(TokenConstants.COMMA);
				}
				parameters.add(interpreter.consume().getValue());
				firstParameter = false;
			}
			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);
		}

		MultiStatementNode root = readMultiStatement();
		return new Script(textID, interpreter.getCurrentFile(), root, parameters);
	}

	// ###################################################################################
	// ################################ Multi Statement ##################################
	// ###################################################################################

	private MultiStatementNode readMultiStatement() throws Exception {
		interpreter.consume(TokenConstants.CURLY_BRACKETS_OPEN);

		List<AbstractScriptNode> nodeList = new ArrayList<>(1);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = interpreter.peek())) { // look at the next token (just look!)
			nodeList.add(readStatement());
		}
		interpreter.consume(TokenConstants.CURLY_BRACKETS_CLOSE);

		return new MultiStatementNode(nodeList);
	}

	private AbstractScriptNode readStatement() throws Exception {
		Token next = interpreter.peek();
		AbstractScriptNode result;

		if (next.getType() == TokenType.KEYWORD) { // keyword
			if (TokenConstants.IF.equals(next)) { // if statement
				return readIfStatement();

			} else if (TokenConstants.WHILE.equals(next)) { // while statement
				return readWhileStatement();

			} else if (TokenConstants.FOR.equals(next)) { // for statement
				return readForStatement();

			} else if (TokenConstants.BREAK.equals(next)) { // break statement
				return readBreakStatement();

			} else if (TokenConstants.RETURN.equals(next)) { // break statement
				return readReturnStatement();

			} else { // expression (induced by eg. 'self')
				result = readExpression();
				interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
				return result;

			}

		} else if (next.getType() == TokenType.COMMAND) { // a command -> expression
			result = readExpression();
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			return result;

		} else if (next.getType() == TokenType.IDENTIFIER || next.getType() == TokenType.OPERATOR || TokenConstants.ROUND_BRACKETS_OPEN.equals(next)) { // also expression
			result = readExpression();
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			return result;

		} else {
			Logger.parsingError("Illegal script command '", next, interpreter);
			interpreter.consume();

		}

		return new NoOperatorNode();
	}

	// ###################################################################################
	// ################################ If Statement #####################################
	// ###################################################################################

	private IfStatementNode readIfStatement() throws Exception {
		interpreter.consume(TokenConstants.IF);
		interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

		AbstractScriptNode condition = readExpression();

		interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

		AbstractScriptNode body;

		if (TokenConstants.CURLY_BRACKETS_OPEN.equals(interpreter.peek())) {
			body = readMultiStatement();

		} else {
			body = readExpression();
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
		}

		AbstractScriptNode elseBody = null;

		Token next = interpreter.peek();
		if (TokenConstants.ELSE.equals(next)) {
			interpreter.consume();
			Token nextNext = interpreter.peek();

			if (TokenConstants.IF.equals(nextNext)) {
				elseBody = readIfStatement();

			} else if (TokenConstants.CURLY_BRACKETS_OPEN.equals(nextNext)) {
				elseBody = readMultiStatement();

			} else {
				elseBody = readExpression();
				interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			}
		}

		return new IfStatementNode(condition, body, elseBody);
	}

	// ###################################################################################
	// ################################ While Statement ##################################
	// ###################################################################################

	private WhileStatementNode readWhileStatement() throws Exception {
		interpreter.consume(TokenConstants.WHILE);
		interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

		AbstractScriptNode expressionNode = readExpression();

		interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

		MultiStatementNode body = readMultiStatement();

		return new WhileStatementNode(expressionNode, body);
	}

	// ###################################################################################
	// ################################ For Statement ####################################
	// ###################################################################################

	private ForStatementNode readForStatement() throws Exception {
		interpreter.consume(TokenConstants.FOR);
		interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

		AbstractScriptNode initial, condition, step;

		initial = readExpression();
		Token next = interpreter.peek();

		if (TokenConstants.COLON.equals(next)) { // special iterator (topology, entities ...)
			interpreter.consume(TokenConstants.COLON);
			AbstractScriptNode iterator = readExpression();
			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

			AbstractScriptNode body;

			if (TokenConstants.CURLY_BRACKETS_OPEN.equals(interpreter.peek())) {
				body = readMultiStatement();

			} else {
				body = readExpression();
				interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			}

			return new ForStatementNode(initial, iterator, body);

		} else { // normal for loop
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			condition = readExpression();
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			step = readExpression();
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);

			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

			AbstractScriptNode body;

			if (TokenConstants.CURLY_BRACKETS_OPEN.equals(interpreter.peek())) {
				body = readMultiStatement();

			} else {
				body = readExpression();
				interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			}

			return new ForStatementNode(initial, condition, step, body);
		}
	}

	// ###################################################################################
	// ################################ Break Statement ##################################
	// ###################################################################################

	private BreakStatementNode readBreakStatement() throws Exception {
		interpreter.consume(TokenConstants.BREAK);
		interpreter.voluntaryConsume(TokenConstants.SEMICOLON);

		return new BreakStatementNode();
	}

	// ###################################################################################
	// ################################ Return Statement ##################################
	// ###################################################################################

	private ReturnStatementNode readReturnStatement() throws Exception {
		interpreter.consume(TokenConstants.RETURN);

		Token next = interpreter.peek();

		// this is possibly a bit sloppy. i want return statements without semicolon, but we still need to find out
		// whether a return value should be read or not. if the next token is ';' or '}', no return value is expected
		if (TokenConstants.SEMICOLON.equals(next) || TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // no return value
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);
			return new ReturnStatementNode();
		}

		boolean brackets = interpreter.voluntaryConsume(TokenConstants.ROUND_BRACKETS_OPEN);

		AbstractScriptNode value = null;
		if (! TokenConstants.ROUND_BRACKETS_CLOSE.equals(interpreter.peek())) {
			value = readExpression();
		}

		if (brackets) { interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE); }
		interpreter.voluntaryConsume(TokenConstants.SEMICOLON);

		return value != null ? new ReturnStatementNode(value) : new ReturnStatementNode();
	}

	// ###################################################################################
	// ################################ Expression #######################################
	// ###################################################################################

	private AbstractScriptNode readExpression() throws Exception {
		Token expression = interpreter.consume();
		TokenConstants command = TokenConstants.getCorrespondingConstantOfType(expression, TokenType.COMMAND);
		AbstractScriptNode left = null;

		if (command != null) { // we have a command!
			List<AbstractScriptNode> parameters = readParameters();
			left = new CommandExpressionNode(expression, parameters);

		} else if (TokenConstants.SELF.equals(expression)) { // a self expression
			left = new SelfNode();

		} else if (TokenConstants.MAIN.equals(expression)) { // a main expression
			left = new MainNode();

		} else if (TokenConstants.TRUE.equals(expression)) { // true = 1.0
			left = new LiteralNode(new Token(TokenType.LITERAL, "1", expression.getLine()));

		} else if (TokenConstants.FALSE.equals(expression)) { // false = 0.0
			left = new LiteralNode(new Token(TokenType.LITERAL, "0", expression.getLine()));

		} else if (TokenConstants.PI.equals(expression)) { // PI
			left = new LiteralNode(new Token(TokenType.LITERAL, String.valueOf(Math.PI), expression.getLine()));

		} else if (TokenConstants.ROUND_BRACKETS_OPEN.equals(expression)) { // an expression in brackets
			left = readExpression();

			if (left != null && left.getClass() == BinaryExpressionNode.class) { // binary expression in brackets
				((BinaryExpressionNode) left).setBracketed(true);
			}

			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);
			interpreter.voluntaryConsume(TokenConstants.SEMICOLON);

		} else if (TokenConstants.SQUARE_BRACKETS_OPEN.equals(expression)) { // a list expression
			List<AbstractScriptNode> nodeList = new ArrayList<>();

			boolean first = true;
			while (!TokenConstants.SQUARE_BRACKETS_CLOSE.equals(interpreter.peek())) {
				if (!first) {
					interpreter.consume(TokenConstants.COMMA);
				}
				nodeList.add(readExpression());
				first = false;
			}
			left = new ListExpressionNode(nodeList);

			interpreter.consume(TokenConstants.SQUARE_BRACKETS_CLOSE);

		} else if (expression.getType() == TokenType.OPERATOR) { // an operator in front of an expression -> unary node
			AbstractScriptNode right = readExpression();
			AbstractScriptNode self = new SelfNode();
			left = precedenceCorrection(expression, self, right, true);

		} else if (expression.getType() == TokenType.LITERAL) { // we have a literal
			left = new LiteralNode(expression);

		} else if (expression.getType() == TokenType.IDENTIFIER) { // we have an identifier
			left = new IdentifierNode(expression);

			if (TokenConstants.ROUND_BRACKETS_OPEN.equals(interpreter.peek())) { // a script call upon an object
				List<AbstractScriptNode> parameters = readParameters();
				left = new ScriptCallNode(expression, parameters);
			}

		} else {
			Logger.parsingError("Unknown script command '",expression, interpreter);

		}

		// Arithmetic recursion
		if (left != null) {
			Token next = interpreter.peek();
			if (next.getType() == TokenType.OPERATOR) {
				return readArithmeticExpression(left);
			}
		} else {

			Logger.parsingError("Empty expression '",expression, interpreter);
			left = new NoOperatorNode();
		}

//		Logger.log("Parsed '" + expression.getValue() + "' on line " + expression.getLine());
//		left.print("   ");

		return left;
	}

	// ###################################################################################
	// ################################ Parameters #######################################
	// ###################################################################################

	private List<AbstractScriptNode> readParameters() throws Exception {
		interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

		List<AbstractScriptNode> parameters = new ArrayList<>(1);

		boolean firstParameter = true;
		while (!TokenConstants.ROUND_BRACKETS_CLOSE.equals(interpreter.peek())) {
			if (!firstParameter) {
				interpreter.consume(TokenConstants.COMMA);
			}
			parameters.add(readExpression());
			firstParameter = false;
		}
		interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

		return parameters;
	}

	// ###################################################################################
	// ################################ Arithmetic Expression ############################
	// ###################################################################################

	private AbstractScriptNode readArithmeticExpression(AbstractScriptNode left) throws Exception {
		Token operator = interpreter.consume();

		if (TokenConstants.SINGLE_INCREMENT.equals(operator) || TokenConstants.SINGLE_DECREMENT.equals(operator)) {
			return new UnaryExpressionNode(operator, left);

		} else {
			AbstractScriptNode right = readExpression();
			return precedenceCorrection(operator, left, right, false);
		}
	}

	// ###################################################################################
	// ################################ Precedence Correction ############################
	// ###################################################################################

	private AbstractScriptNode precedenceCorrection(Token operator, AbstractScriptNode left, AbstractScriptNode right, boolean unary) throws Exception {
		if (right.getClass() == BinaryExpressionNode.class) { // possibly rehang tree structure
			BinaryExpressionNode rightNode = ((BinaryExpressionNode) right);

			if (!rightNode.isBracketed()) { // only correct when right node is not bracketed
				// current operator binds more strongly than the one from the right expression -> rehang
				if (operator.getPrecedence() <= rightNode.getOperator().getPrecedence()) {
					if (unary) {
						AbstractScriptNode sub = rightNode.getLeft();
						AbstractScriptNode newLeft = precedenceCorrection(operator, left, sub, true);
						rightNode.setLeft(newLeft);
						return rightNode;
					}

					AbstractScriptNode sub = rightNode.getLeft();
					AbstractScriptNode newLeft = precedenceCorrection(operator, left, sub, false);
					rightNode.setLeft(newLeft);
					return rightNode;
				}
			}
		} else if (right.getClass() == UnaryExpressionNode.class) { // possibly need to rehang
			UnaryExpressionNode rightNode = ((UnaryExpressionNode) right);

			// current operator binds more strongly than the one from the right expression -> rehang
			if (operator.getPrecedence() <= rightNode.getOperator().getPrecedence()) {
				if (unary) {
					AbstractScriptNode sub = rightNode.getSubNode();
					AbstractScriptNode newSub = precedenceCorrection(operator, left, sub, true);
					rightNode.setSubNode(newSub);
					return rightNode;
				}

				AbstractScriptNode sub = rightNode.getSubNode();
				AbstractScriptNode newSub = precedenceCorrection(operator, left, sub, false);
				rightNode.setSubNode(newSub);
				return rightNode;
			}
		}

		if (unary) {
			return new UnaryExpressionNode(operator, right);
		}

		return new BinaryExpressionNode(operator, left, right);
	}

}
