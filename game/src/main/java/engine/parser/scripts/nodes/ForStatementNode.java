package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Tile;
import engine.data.Data;
import engine.data.Script;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

public class ForStatementNode extends AbstractScriptNode {

	private Token iterator = null;

	public ForStatementNode(AbstractScriptNode initial, AbstractScriptNode condition, AbstractScriptNode step, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[4];
		subNodes[0] = initial;
		subNodes[1] = condition;
		subNodes[2] = step;
		subNodes[3] = body;
	}

	public ForStatementNode(AbstractScriptNode initial, Token iterator, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[2];
		subNodes[0] = initial;
		subNodes[1] = body;
		this.iterator = iterator;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable body = new Variable();
		if (iterator == null) { // normal for loop
			subNodes[0].execute(instance, script); // initial
			while (!subNodes[1].execute(instance, script).isNull()) { // condition
				body = subNodes[3].execute(instance, script); // body
				subNodes[2].execute(instance, script); // step
			}

		} else {
			if (TokenConstants.ITERATOR_TILE.equals(iterator)) { // tile iterator
				Variable iterationVariable = subNodes[0].execute(instance, script); // initial
				if (Data.getPlanet() != null) {
					for (Face face : Data.getPlanet().getFaces()) {
						for (Tile tile : face.getTiles()) {
							iterationVariable.setTile(tile);
							body = subNodes[1].execute(instance, script); // body
						}
					}
				}
			} else {
				Logger.error("Illegal keyword '" + iterator.getValue() + "' as for-loop iterator!");
			}
		}
		return body;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "For Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + "-");
		subNodes[2].print(indentation + "-");
		subNodes[3].print(indentation + ".");
	}
}
