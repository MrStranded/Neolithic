package engine.parser.scripts.nodes;

import engine.data.Script;
import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;
import engine.parser.utils.TokenNumerifier;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public class ListExpressionNode extends AbstractScriptNode {

	public ListExpressionNode(List<AbstractScriptNode> elementList) {
		subNodes = new AbstractScriptNode[elementList.size()];
		for (int i=0; i<elementList.size(); i++) {
			subNodes[i] = elementList.get(i);
		}
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		List<Variable> variableList = new ArrayList<>(subNodes.length);
		for (int i=0; i<subNodes.length; i++) {
			variableList.add(subNodes[i].execute(instance, script));
		}
		return new Variable(variableList);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "List:");
		for (AbstractScriptNode element : subNodes) {
			element.print(indentation + " ");
		}
	}
}
