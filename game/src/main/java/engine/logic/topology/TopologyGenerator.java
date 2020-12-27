package engine.logic.topology;

import constants.TopologyConstants;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.Data;
import engine.data.proto.TileContainer;
import engine.data.variables.DataType;

import java.util.List;

public class TopologyGenerator {

	public static void formTopology(Planet planet) {
		int size = planet.getSize();

		for (int i=0; i<100; i++) {
			int faceX = (int) (5d * Math.random());
			int faceY = (int) (4d * Math.random());

			int tileX = (int) ((double) size * Math.random());
			int tileY = (int) ((double) size * Math.random());

			int height = (int) (128d + 128d * Math.random());

			Tile tile = planet.getFace(faceX, faceY).getTile(tileX, tileY);

			liftTile(tile, height);
		}

		for (int i=0; i<100; i++) {
			int faceX = (int) (5d * Math.random());
			int faceY = (int) (4d * Math.random());

			int tileX = (int) ((double) size * Math.random());
			int tileY = (int) ((double) size * Math.random());

			Tile tile = planet.getFace(faceX, faceY).getTile(tileX, tileY);

			if (tile.getHeight() > 110) {
				carveRiver(tile);
			}
		}

		fitTiles(planet, Data.getContainersOfType(DataType.TILE));
	}

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
				int bestFit = calculateBestTile(tile.getHeight(), tileList);

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
			int distance = Math.abs(height - protoTile.getPreferredHeight()) + (int) ((double) protoTile.getPreferredHeightBlur() * (Math.random()*2d - 1d));
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

	private static void liftTile(Tile tile, int height) {
		tile.setHeight(height);

		if (height > 5) {
			for (Tile t : Neighbour.getNeighbours(tile)) {
				int deviation = (int) (20d * Math.random());

				if (t.getHeight() < height -deviation) {
					liftTile(t, height - deviation);
				}
			}
		}
	}

	private static void carveRiver(Tile tile) {
		int riverdepth = 2;
		int waterdepth = 1;

		int height = tile.getHeight();
		int waterHeight = height - waterdepth;
		if (waterHeight < TopologyConstants.PLANET_OCEAN_HEIGHT) {
			waterHeight = TopologyConstants.PLANET_OCEAN_HEIGHT;
		}

		tile.setHeight(height - riverdepth);
		tile.setWaterHeight(waterHeight);

		Tile lowest = null;
		int lowestHeight = height;

		for (Tile t : Neighbour.getNeighbours(tile)) {
			if ((t.getHeight() <= lowestHeight) && (t.getWaterHeight() < waterHeight)) {
				lowest = t;
				lowestHeight = t.getHeight();
			}
		}

		if (lowest != null) {
			carveRiver(lowest);
		}
	}
}
