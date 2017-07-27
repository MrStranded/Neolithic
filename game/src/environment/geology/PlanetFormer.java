package environment.geology;

import environment.world.Entity;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Tile;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class forms hills and mountains on a mountain.
 */
public class PlanetFormer {

	/**
	 * Generates trees on the whole planet.
	 */
	public static void generateTrees(Planet planet) {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) {
					for (int x=0; x<face.getSize(); x++) {
						for (int y=0; y<face.getSize(); y++) {
							if (Math.random()>0.7d) {
								face.getTile(x,y).addEntity(new Entity(face.getTile(x,y),'T'));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Generates a Topology for the whole planet.
	 */
	public static void generateTopology(Planet planet) {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) generateFaceTopology(face);
			}
		}
	}

	private static void generateFaceTopology(Face face) {
		int size = face.getSize();
		int hills = 8;
		int hillSize = 12;
		int stepSize = 2;
		int elevation = 100;

		int x=0,y=0;

		for (int j=0; j<hills; j++) {
			x = (int) (size * Math.random());
			y = (int) (size * Math.random());
			for (int i = 0; i < hillSize; i++) {
				x = (x + (int) (stepSize * Math.random())) % size;
				y = (y + (int) (stepSize * Math.random())) % size;
				elevateTile(face, face.getTile(x, y), (int) (elevation*Math.random()));
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

	private static void elevateTile(Face face, Tile tile, int level) {
		if (level>255) level = 255;
		if (tile.getHeight() < level) {
			tile.setHeight(level);

			level -= ((Math.random()+0.5d)*8);
			if (level > 0) {
				Tile[] neighbours = face.getNeighbours(tile.getX(), tile.getY());
				for (Tile t : neighbours) {
					if (t != null) elevateTile(t.getFace(), t, level);
				}
			}
		}
	}

}
