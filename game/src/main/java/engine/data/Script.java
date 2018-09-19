package engine.data;

import engine.data.IDInterface;
import engine.data.entities.Instance;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.Variable;
import engine.parser.scripts.nodes.AbstractScriptNode;
import engine.utils.converters.StringConverter;

import java.util.List;
import java.util.Stack;

public class Script implements IDInterface {

	private String textId;
	private int id = -1;
	private AbstractScriptNode root;
	private String[] parameterNames;
	private Stack<BinaryTree<Variable>> variableStack;

	public Script(String textId, AbstractScriptNode root, List<String> parameterNameList) {
		this.textId = textId;
		this.root = root;

		id = StringConverter.toID(textId);

		parameterNames = new String[parameterNameList.size()];
		int i=0;
		for (String parameterName : parameterNameList) {
			parameterNames[i++] = parameterName;
		}

		variableStack = new Stack<>();
	}

	public Variable run(Instance self, Variable[] parameters) {
		// create new variable tree
		BinaryTree<Variable> variables = new BinaryTree<>();
		variableStack.push(variables);

		// fill parameters in
		if (parameters != null) {
			int i = 0;
			for (Variable parameter : parameters) {
				if (parameter != null && i < parameterNames.length) {
					Variable variable = new Variable(parameterNames[i], parameter);
					variables.insert(variable);
				}
				i++;
			}
		}

		// debugging
		if (false) {
			System.out.println("---------------------------- S");
			System.out.println("Running script " + textId);
			System.out.println("Instance Variables:");
			self.printVariables();
			System.out.println("Script Variables:");
			if (!variableStack.empty()) {
				for (IDInterface variable : variableStack.peek().toArray()) {
					System.out.println(((Variable) variable));
				}
			}
			System.out.println("---------------------------- E");
		}

		// execute
		Variable result = root.execute(self, this);

		// pop variable stack
		variableStack.pop();

		return result;
	}

	public Variable getVariable(String name) {
		if (!variableStack.empty()) {
			BinaryTree<Variable> variables = variableStack.peek();
			return variables.get(StringConverter.toID(name));
		} else {
			return new Variable();
		}
	}
	public void addVariable(Variable variable) {
		if (!variableStack.empty()) {
			variableStack.peek().insert(variable);
		}
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return other; // overwrite older scripts with newer ones
	}

	public void print() {
		System.out.println("Script: " + textId + " (id = " + getId() + ") ---------------------");
		for (String parameter : parameterNames) {
			System.out.println("-> " + parameter);
		}
		root.print("    ");
	}
}
