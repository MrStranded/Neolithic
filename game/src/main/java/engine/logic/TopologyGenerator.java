package engine.logic;

import constants.TopologyConstants;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.graphics.renderer.color.RGBA;

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

		for (Face face : planet.getFaces()) {
			for (Tile tile : face.getTiles()) {
				int height = tile.getHeight();

				if (height > TopologyConstants.PLANET_OZEAN_HEIGHT) {
					if (height > 200) {
						double p = 1d - (255d - height) / 55d;

						tile.setColor(new RGBA(
								0.5d + p*0.5d,
								1d,
								0.25d + p*0.75d
						));
					} else {
						double p = 1d - (200d - height) / (200 - TopologyConstants.PLANET_OZEAN_HEIGHT);

						tile.setColor(new RGBA(
								1d - p*0.5d,
								0.75d + p*0.25d,
								0.25d
						));
					}
				}
			}
		}
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
		if (waterHeight < TopologyConstants.PLANET_OZEAN_HEIGHT) {
			waterHeight = TopologyConstants.PLANET_OZEAN_HEIGHT;
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
