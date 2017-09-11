package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A scriptblock may be attached to an entity or to another scriptblock.
 *
 * Created by Michael on 05.09.2017.
 */
public class ScriptBlock {

	private Expression expression = null;
	private ConcurrentLinkedDeque<ScriptBlock> scriptBlocks = null;

	public ScriptBlock(String code) {
		expression = ExpressionBuilder.parseLine(code);
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void addScriptBlock(ScriptBlock scriptBlock) {
		if (scriptBlocks == null) scriptBlocks = new ConcurrentLinkedDeque<ScriptBlock>();
		scriptBlocks.add(scriptBlock);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public ConcurrentLinkedDeque<ScriptBlock> getScriptBlocks() {
		return scriptBlocks;
	}

	public String toString() {
		if (expression == null) return "-no expression-";
		return expression.toString();
	}

}
