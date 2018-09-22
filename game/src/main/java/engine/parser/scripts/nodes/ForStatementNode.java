package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Tile;
import engine.data.Data;
import engine.data.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.List;

public class ForStatementNode extends AbstractScriptNode {

	public ForStatementNode(AbstractScriptNode initial, AbstractScriptNode condition, AbstractScriptNode step, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[4];
		subNodes[0] = initial;
		subNodes[1] = condition;
		subNodes[2] = step;
		subNodes[3] = body;
	}

	public ForStatementNode(AbstractScriptNode initial, AbstractScriptNode iterator, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[3];
		subNodes[0] = initial;
		subNodes[1] = iterator;
		subNodes[2] = body;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable body = new Variable();
		if (subNodes.length == 4) { // normal for loop
			subNodes[0].execute(instance, script); // initial
			while (!subNodes[1].execute(instance, script).isNull()) { // condition
				body = subNodes[3].execute(instance, script); // body
				subNodes[2].execute(instance, script); // step
			}

		} else { // for loop with list iterator
			Variable iterationVariable = subNodes[0].execute(instance, script); // initial
			Variable listVariable = subNodes[1].execute(instance, script); // iterator
			if (listVariable.getType() == DataType.LIST) {
				List<Variable> variableList = listVariable.getList();
				if (variableList != null) {
					for (Variable variable : variableList) {
						iterationVariable.copyValue(variable);
						body = subNodes[2].execute(instance, script); // body
					}
				}
			} else {
				Logger.error("Iterator variable has to be of type LIST! Encountered type '" + listVariable.getType() + "'!");
			}
		}
		return body;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "For Statement");
		for (int i=0; i<subNodes.length; i++) {
			subNodes[i].print(indentation + "-");
		}
	}
}
