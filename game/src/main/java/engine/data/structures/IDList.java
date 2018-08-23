package engine.data.structures;

import java.util.ArrayList;
import java.util.List;

public class IDList {

	private List<String> ids;

	public IDList() {
		ids = new ArrayList<>(4);
	}

	public void add(String textId) {
		if (textId == null) {
			return;
		}

		for (String id : ids) {
			if (id.equals(textId)) {
				return;
			}
		}

		ids.add(textId);
	}

	public List<String> getIds() {
		return ids;
	}

}
