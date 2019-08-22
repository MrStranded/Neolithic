package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;

import java.util.List;

public class MultiStatementNode extends AbstractScriptNode {

	public MultiStatementNode(List<AbstractScriptNode> nodeList) {
		subNodes = new AbstractScriptNode[nodeList.size()];
		int i=0;
		for (AbstractScriptNode node : nodeList) {
			subNodes[i++] = node;
		}
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		boolean allTrue = true;

		try {
            for (AbstractScriptNode node : subNodes) {
                Variable variable = node.execute(instance, script);
                if (node.getClass() == CommandExpressionNode.class) {
                    if (TokenConstants.REQUIRE.equals(((CommandExpressionNode) node).getCommand())) {
                        allTrue = !variable.isNull() && allTrue; // allTrue is only true if all require command subNodes return Variables that are not null
                    }
                }
            }
        } catch (ReturnException returnException) {
		    return returnException.getReturnValue();
        }

		return new Variable(allTrue ? 1 : 0);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "MultiStatement");
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + ">");
		}
	}
}
