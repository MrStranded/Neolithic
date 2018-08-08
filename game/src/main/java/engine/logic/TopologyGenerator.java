package engine.logic;

import engine.data.Planet;
import engine.data.Tile;
import engine.math.numericalObjects.Matrix4;

public class TopologyGenerator {

	public static void formTopology(Planet planet) {
		int size = planet.getSize();

		for (int i=-90; i<=90; i+=1) {
			long time = System.nanoTime();
			Tile tile = GeographicCoordinates.getTile(planet, i*2, i);
			System.out.println("geo search took: " + (System.nanoTime()-time)/1000000d + " ms");
			tile.setHeight(255);
		}
		//liftTile(planet.getFace(0,0).getTile(0,0),255);
		/*
		for (int i=0; i<100; i++) {
			int faceX = (int) (5d * Math.random());
			int faceY = (int) (4d * Math.random());

			int tileX = (int) ((double) size * Math.random());
			int tileY = (int) ((double) size * Math.random());

			int height = (int) (128d + 128d * Math.random());

			Tile tile = planet.getFace(faceX, faceY).getTile(tileX, tileY);

			liftTile(tile, height);
		}
		*/
	}

	private static void liftTile(Tile tile, int height) {
		tile.setHeight(height);

		if (height > 10) {
			Tile[] neighbours = Neighbour.getNeighbours(tile);

			for (Tile t : neighbours) {
				if (t.getHeight() < height -10) {
					liftTile(t, height - 10);
				}
			}
		}
	}
}
