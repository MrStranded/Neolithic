package environment.geology;

import environment.Face;
import environment.Planet;
import environment.Tile;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class forms hills and mountains on a mountain.
 */
public class PlanetFormer {

	public static void generateTopology(Planet planet) {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) generateFaceTopology(face);
			}
		}
	}

	private static void generateFaceTopology(Face face) {
		int size = face.getSize();
		int hills = 16;
		int hillSize = 12;
		int stepSize = 4;
		int elevation = 42;

		int x=0,y=0;

		for (int j=0; j<hills; j++) {
			x = (int) (size * Math.random());
			y = (int) (size * Math.random());
			for (int i = 0; i < hillSize; i++) {
				x = (x + (int) (stepSize * Math.random())) % size;
				y = (y + (int) (stepSize * Math.random())) % size;
				raiseTile(face, face.getTile(x, y), elevation);
			}
		}
	}

	private static void raiseTile(Face face, Tile tile, int level) {
		int h = tile.getHeight()+level;
		if (h>255) h = 255;
		tile.setHeight(h);

		level *= (Math.random()/4d+0.375d);
		if (level>0) {
			Tile[] neighbours = face.getNeighbours(tile.getX(),tile.getY());
			for (Tile t : neighbours) {
				if (t!=null) raiseTile(t.getFace(),t,level);
			}
		}
	}

}
