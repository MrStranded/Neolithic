package engine.data.structures;

import engine.data.IDInterface;
import engine.data.entities.Instance;
import engine.parser.scripts.nodes.AbstractScriptNode;

public class Script implements IDInterface {

	private String textId;
	private int id = -1;
	private AbstractScriptNode root;

	public Script(String textId, AbstractScriptNode root) {
		this.textId = textId;
		this.root = root;
	}

	public void run(Instance self) {

	}

	@Override
	public int getId() {
		if (id == -1 && textId != null) {
			int i = 1;
			for (char c : textId.toCharArray()) {
				id += c * i;
				i++;
			}
		}
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return this;
	}

	public void print() {
		System.out.println("Script: " + textId + " (" + getId() + ") ---------------");
		root.print();
	}
}
