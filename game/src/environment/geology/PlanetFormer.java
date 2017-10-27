package environment.geology;

import data.Data;
import data.personal.Attribute;
import data.proto.Container;
import data.proto.ProtoAttribute;
import engine.EntityBuilder;
import environment.world.Entity;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Tile;
import threads.DependantThread;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class forms hills and mountains on a mountain.
 */
public class PlanetFormer extends DependantThread {

	private static Planet planet;
	public static void setPlanet (Planet p) {
		planet = p;
	}

	private static int size = 1;

	private static int defaultTileId = -1;
	private static int defaultTileHeight = 0;

	private static double mountainPercent = 0;
	private static int mountainMinHeight = 0;
	private static int mountainMaxHeight = 0;

	private static double valleyPercent = 0;
	private static int valleyMinHeight = 0;
	private static int valleyMaxHeight = 0;

	public void run () {
		waitForDependantThread();

		initializeGenerationValues();

		System.out.println("Starting topology generation.");
		generateTopology();
		System.out.println("Planting trees.");
		generateTrees();
		System.out.println("Growing life forms.");
		generateCreatures();
		System.out.println("Planet formation completed.");
	}

	private static void initializeGenerationValues() {
		if (planet != null) size = planet.getFace(0).getSize();

		Container worldGen = Data.getContainer(Data.getContainerId("gen01"));
		if (worldGen != null) {
			defaultTileId = Data.getContainerId(worldGen.getString("defaultTile"));
			defaultTileHeight = worldGen.getInt("defaultTileHeight");

			mountainPercent = Double.parseDouble(worldGen.getString("mountainPercent"));
			mountainMinHeight = worldGen.getInt("mountainHeight",0);
			mountainMaxHeight = worldGen.getInt("mountainHeight",1);

			valleyPercent = Double.parseDouble(worldGen.getString("valleyPercent"));
			valleyMinHeight = worldGen.getInt("valleyDepth",0);
			valleyMaxHeight = worldGen.getInt("valleyDepth",1);
		}
	}

	/**
	 * Generates a handful of creatures on face(0).
	 */
	public static void generateCreatures() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			Face face = planet.getFace(0);
			if (face != null) {

				int speedId = Data.getProtoAttributeId("attSpeed");

				Tile tile = face.getTile((int) (Math.random()*face.getSize()),(int) (Math.random()*face.getSize()));
				EntityBuilder.createEntity(tile,"cSemira");

				for (int i=0; i<7; i++) {
					tile = face.getTile((int) (Math.random()*face.getSize()),(int) (Math.random()*face.getSize()));
					EntityBuilder.createEntity(tile,"cMichi");
				}
			}
		}
	}

	/**
	 * Generates trees on the whole planet.
	 */
	public static void generateTrees() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) {
					for (int x=0; x<face.getSize(); x++) {
						for (int y=0; y<face.getSize(); y++) {
							if ((face.getTile(x,y).getHeight()>100) && (Math.random()>0.95d)) {
								EntityBuilder.createEntity(face.getTile(x,y),"oTree");
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
	public static void generateTopology() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			// ------------------------- defaults
			for (Face face : planet.getFaces()) {
				if (face != null) generateDefaultFaceTopology(face);
			}

			// ------------------------- features
			for (Face face : planet.getFaces()) {
				if (face != null) generateFaceTopology(face);
			}
		}
	}

	private static void generateDefaultFaceTopology(Face face) {
		for (int tx = 0; tx < size; tx++) {
			for (int ty = 0; ty < size; ty++) {
				Tile tile = face.getTile(tx, ty);

				tile.setHeight(defaultTileHeight);

				if (defaultTileId != -1) {
					EntityBuilder.setTileEntity(tile, defaultTileId);
				}
			}
		}
	}

	private static void generateFaceTopology(Face face) {
		for (int tx=0; tx<size; tx++) {
			for (int ty = 0; ty < size; ty++) {
				Tile tile = face.getTile(tx, ty);

				if (Math.random() * 100d < mountainPercent) {
					elevateTile(face, tile, mountainMinHeight + (int) (Math.random()*(mountainMaxHeight-mountainMinHeight)));
				}
				if (Math.random() * 100d < valleyPercent) {
					sinkTile(face, tile, valleyMinHeight + (int) (Math.random()*(valleyMaxHeight-valleyMinHeight)));
				}
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

	private static void sinkTile(Face face, Tile tile, int level) {
		if (level<0) level = 0;
		if (tile.getHeight() > level) {
			tile.setHeight(level);

			level += ((Math.random()+0.5d)*8);
			if (level < 255) {
				Tile[] neighbours = face.getNeighbours(tile.getX(), tile.getY());
				for (Tile t : neighbours) {
					if (t != null) sinkTile(t.getFace(), t, level);
				}
			}
		}
	}

}
