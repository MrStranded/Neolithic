package engine.data.structures;

import engine.data.IDInterface;
import engine.data.entities.Instance;

public class Script implements IDInterface {

	private String textId;
	private int id;

	public void run(Instance self) {

	}

	@Override
	public int getId() {
		if ((textId != null) && (id == 0)) {
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
}
