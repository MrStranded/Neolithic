package engine.logic;

import constants.TopologyConstants;
import engine.data.Face;
import engine.data.Planet;
import engine.data.Tile;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix4;

public class TopologyGenerator {

	public static void formTopology(Planet planet) {
		int size = planet.getSize();

		/*for (double i=-90d; i<=90d; i+=1d) {
			Tile tile = GeographicCoordinates.getTile(planet, i*2, i);
			tile.setHeight(255);
		}*/
		//liftTile(planet.getFace(0,0).getTile(0,0),255);
		///*
		for (int i=0; i<100; i++) {
			int faceX = (int) (5d * Math.random());
			int faceY = (int) (4d * Math.random());

			int tileX = (int) ((double) size * Math.random());
			int tileY = (int) ((double) size * Math.random());

			int height = (int) (128d + 128d * Math.random());

			Tile tile = planet.getFace(faceX, faceY).getTile(tileX, tileY);

			liftTile(tile, height);
		}
		//*/

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
						double p = 1d - (200d - height) / 100d;

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
			Tile[] neighbours = Neighbour.getNeighbours(tile);

			for (Tile t : neighbours) {
				int deviation = (int) (20d * Math.random());

				if (t.getHeight() < height -deviation) {
					liftTile(t, height - deviation);
				}
			}
		}
	}
}
