package engine.parser.scripts;

import engine.data.structures.Script;
import engine.parser.Logger;
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
		MultiStatementNode root = readMultiStatement();
		return new Script(textID, root);
	}

	// ###################################################################################
	// ################################ Multi Statement ##################################
	// ###################################################################################

	private MultiStatementNode readMultiStatement() throws Exception {
		interpreter.consume(TokenConstants.CURLY_BRACKETS_OPEN);

		List<AbstractScriptNode> nodeList = new ArrayList<>(1);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = interpreter.peek())) { // look at the next token (just look!)
			if (next.getType() == TokenType.KEYWORD) { // keyword
				if (TokenConstants.IF.equals(next)) { // if statement
					nodeList.add(readIfStatement());

				} else if (TokenConstants.WHILE.equals(next)) { // while statement
					nodeList.add(readWhileStatement());

				} else if (TokenConstants.FOR.equals(next)) { // for statement
					nodeList.add(readForStatement());

				} else { // expression
					nodeList.add(readExpression());
					interpreter.consume(TokenConstants.SEMICOLON);

				}

			} else if (next.getType() == TokenType.IDENTIFIER) { // also expression
				nodeList.add(readExpression());
				interpreter.consume(TokenConstants.SEMICOLON);

			} else {
				Logger.error("Illegal Script command '" + next.getValue() + "' on line " + next.getLine());
				interpreter.consume();

			}
		}
		interpreter.consume(TokenConstants.CURLY_BRACKETS_CLOSE);

		return new MultiStatementNode(nodeList);
	}

	// ###################################################################################
	// ################################ If Statement #####################################
	// ###################################################################################

	private IfStatementNode readIfStatement() throws Exception {
		interpreter.consume(TokenConstants.IF);
		interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

		AbstractScriptNode expressionNode = readExpression();

		interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

		MultiStatementNode body = readMultiStatement();
		MultiStatementNode elseBody = null;

		Token next = interpreter.peek();

		if (TokenConstants.ELSE.equals(next)) {
			interpreter.consume();
			elseBody = readMultiStatement();

		} else {
			Logger.error("If statement has to continue with 'else' or end. Found unexpected '" + next.getValue() + "' on line " + next.getLine());

		}

		return new IfStatementNode(expressionNode, body, elseBody);
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

		AbstractScriptNode initial = readExpression();
		interpreter.consume(TokenConstants.SEMICOLON);

		AbstractScriptNode condition = readExpression();
		interpreter.consume(TokenConstants.SEMICOLON);

		AbstractScriptNode step = readExpression();

		interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

		MultiStatementNode body = readMultiStatement();

		return new ForStatementNode(initial, condition, step, body);
	}

	// ###################################################################################
	// ################################ Expression #######################################
	// ###################################################################################

	private AbstractScriptNode readExpression() throws Exception {
		Token expression = interpreter.consume();
		TokenConstants command = TokenConstants.getCorrespondingKeyWord(expression);

		if (command != null) { // we have a command!
			interpreter.consume(TokenConstants.ROUND_BRACKETS_OPEN);

			List<AbstractScriptNode> parameters = new ArrayList<>(1);

			boolean firstParameter = true;
			Token next;
			while (!TokenConstants.ROUND_BRACKETS_CLOSE.equals(next = interpreter.peek())) {
				if (!firstParameter) {
					interpreter.consume(TokenConstants.COMMA);
				}
				parameters.add(readExpression());
				firstParameter = false;
			}
			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

			return new CommandExpressionNode(command.getToken(), parameters);

		} else if (TokenConstants.ROUND_BRACKETS_OPEN.equals(expression)) { // an expression in brackets
			AbstractScriptNode expressionNode = readExpression();

			interpreter.consume(TokenConstants.ROUND_BRACKETS_CLOSE);

			Token next = interpreter.peek();
			if (next.getType() == TokenType.OPERATOR) {
				next = interpreter.consume();
				if (TokenConstants.SINGLE_INCREMENT.equals(next) || TokenConstants.SINGLE_DECREMENT.equals(next)) {
					return new UnaryExpressionNode(next, expressionNode);

				} else {
					AbstractScriptNode right = readExpression();
					return new BinaryExpressionNode(next, expressionNode, right);

				}

			} else {
				return expressionNode;

			}

		} else if (expression.getType() == TokenType.OPERATOR) { // an operator in front of an expression -> unary node
			AbstractScriptNode right = readExpression();
			return new UnaryExpressionNode(expression, right);

		} else if (expression.getType() == TokenType.LITERAL || expression.getType() == TokenType.IDENTIFIER) { // we have a literal or identifier
			Token next = interpreter.peek();
			AbstractScriptNode left;
			if (expression.getType() == TokenType.LITERAL) {
				left = new LiteralNode(expression);
			} else {
				left = new IdentifierNode(expression);
			}

			if (next.getType() == TokenType.OPERATOR) {
				next = interpreter.consume();
				if (TokenConstants.SINGLE_INCREMENT.equals(next) || TokenConstants.SINGLE_DECREMENT.equals(next)) {
					return new UnaryExpressionNode(next, left);

				} else {
					AbstractScriptNode right = readExpression();
					return new BinaryExpressionNode(next, left, right);

				}

			} else {
				if (expression.getType() == TokenType.LITERAL) {
					return new LiteralNode(expression);
				} else {
					return new IdentifierNode(expression);
				}

			}

		} else {
			Logger.error("Unknown script command '" + expression.getValue() + "' on line " + expression.getLine());

		}
		return null;
	}

}
