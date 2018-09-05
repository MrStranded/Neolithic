package engine.data.structures;

import engine.data.IDInterface;
import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.scripts.nodes.AbstractScriptNode;
import engine.utils.converters.StringConverter;

import java.util.List;

public class Script implements IDInterface {

	private String textId;
	private int id = -1;
	private AbstractScriptNode root;
	private String[] parameterNames;

	public Script(String textId, AbstractScriptNode root, List<String> parameterNameList) {
		this.textId = textId;
		this.root = root;

		id = StringConverter.toID(textId);

		parameterNames = new String[parameterNameList.size()];
		int i=0;
		for (String parameterName : parameterNameList) {
			parameterNames[i++] = parameterName;
		}
	}

	public void run(Instance self, Variable[] parameters) {
		if (parameters != null) {
			int i = 0;
			for (Variable parameter : parameters) {
				if (parameter != null && i < parameterNames.length) {
					Variable variable = new Variable(parameterNames[i], parameter);
					self.addVariable(variable);
				}
				i++;
			}
		}
		System.out.println("Running script " + textId);
		root.execute(self);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return this;
	}

	public void print() {
		System.out.println("Script: " + textId + " (id = " + getId() + ") ---------------------");
		for (String parameter : parameterNames) {
			System.out.println("-> " + parameter);
		}
		root.print("    ");
	}
}
