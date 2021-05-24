package engine.logic.topology;

import engine.data.entities.Tile;
import engine.data.variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class TileArea {

	private List<Tile> tileList;
	private Tile center;
	private int radius;

	public TileArea(Tile center, int radius) {
		this.center = center;
		this.radius = radius;
		tileList = new ArrayList<>();
		addTile(center, radius);
	}

	private void addTile(Tile tile, int remainingRadius) {
		tileList.add(tile);
		if (remainingRadius > 0) {
			Tile[] neighbours = Neighbour.getNeighbours(tile);
			for (Tile neighbour : neighbours) {
				if (!tileList.contains(neighbour)) {
					addTile(neighbour, remainingRadius - 1);
				}
			}
		}
	}

	public List<Tile> getTileList() {
		return tileList;
	}

	public List<Variable> getTilesAsVariableList() {
		List<Variable> variableList = new ArrayList<>(tileList.size());
		for (Tile tile : tileList) {
			variableList.add(new Variable(tile));
		}
		return variableList;
	}

}
