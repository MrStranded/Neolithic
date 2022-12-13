package engine.logic.topology;

import engine.data.Data;
import engine.data.entities.Tile;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.proto.Container;
import engine.data.proto.TileContainer;

import java.util.List;

public class TopologyGenerator {

	/**
	 * Iterates over each tile and assigns best fitting tile type from given list to it.
	 * @param planet to fit topology on
	 */
	public static void fitTiles(Planet planet, List<Container> tileList) {
		if (tileList == null) {
			return;
		}

		for (Face face : planet.getFaces()) {
			for (Tile tile : face.getTiles()) {
				int height = tile.getHeight();
				int bestFit = calculateBestTile(height, tileList);

				if (bestFit >= 0) {
					tile.change(bestFit);
				}
			}
		}
	}

	/**
	 * Calculates the best tile type for a given environment.
	 * @param height
	 * @param tileList
	 * @return
	 */
	private static int calculateBestTile(int height, List<Container> tileList) {
		Container closest = null;
		int closestDistance = 255;

		for (Container container : tileList) {
			TileContainer protoTile = (TileContainer) container;
			int preferredHeight = protoTile.getPreferredHeight();// + (int) ((double) protoTile.getPreferredHeightBlur() * (Math.random()*2d - 1d));
			int distance = Math.abs(height - preferredHeight);

			if (closest == null || distance < closestDistance) {
				closest = protoTile;
				closestDistance = distance;
			}
		}

		if (closest != null) {
			return Data.getContainerID(closest.getTextID());
		}
		return -1;
	}

}
