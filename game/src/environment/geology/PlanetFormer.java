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

	private static int defaultFluidId = -1;
	private static int defaultFluidHeight = 0;

	private static double mountainPercent = 0;
	private static double mountainContinuationPercent = 0;
	private static int mountainMinHeight = 0;
	private static int mountainMaxHeight = 0;
	private static int mountainDeltaHeight = 8;
	private static int mountainDeltaDerivation = 0;

	private static double valleyPercent = 0;
	private static double valleyContinuationPercent = 0;
	private static int valleyMinHeight = 0;
	private static int valleyMaxHeight = 0;
	private static int valleyDeltaHeight = 8;
	private static int valleyDeltaDerivation = 8;

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

			defaultFluidId = Data.getContainerId(worldGen.getString("defaultFluid"));
			defaultFluidHeight = worldGen.getInt("defaultFluidHeight");

			mountainPercent = Double.parseDouble(worldGen.getString("mountainPercent",0));
			mountainContinuationPercent = Double.parseDouble(worldGen.getString("mountainPercent",1));
			mountainMinHeight = worldGen.getInt("mountainHeight",0);
			mountainMaxHeight = worldGen.getInt("mountainHeight",1);
			mountainDeltaHeight = worldGen.getInt("mountainHeight",2);
			mountainDeltaDerivation = worldGen.getInt("mountainHeight",3);

			valleyPercent = Double.parseDouble(worldGen.getString("valleyPercent",0));
			valleyContinuationPercent = Double.parseDouble(worldGen.getString("valleyPercent",1));
			valleyMinHeight = worldGen.getInt("valleyDepth",0);
			valleyMaxHeight = worldGen.getInt("valleyDepth",1);
			valleyDeltaHeight = worldGen.getInt("valleyDepth",2);
			valleyDeltaDerivation = worldGen.getInt("valleyDepth",3);

			if (mountainDeltaHeight <= 0) mountainDeltaHeight = 8;
			if (valleyDeltaHeight <= 0) valleyDeltaHeight = 8;
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

			// ------------------------- default fluids
			for (Face face : planet.getFaces()) {
				if (face != null) generateDefaultFluidFaceTopology(face);
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

	private static void generateDefaultFluidFaceTopology(Face face) {
		if (defaultFluidId < 0) return;

		for (int tx = 0; tx < size; tx++) {
			for (int ty = 0; ty < size; ty++) {
				Tile tile = face.getTile(tx, ty);

				if (tile.getHeight() < defaultFluidHeight) {
					Entity fluid = EntityBuilder.createEntity(tile,defaultFluidId);
					fluid.setAmount(defaultFluidHeight-tile.getHeight());
				}
			}
		}
	}

	private static void generateFaceTopology(Face face) {
		for (int tx=0; tx<size; tx++) {
			for (int ty = 0; ty < size; ty++) {
				Tile tile = face.getTile(tx, ty);

				if (Math.random() * 100d < mountainPercent) {
					changeTileHeight(face, tile,
							mountainMinHeight + (int) (Math.random()*(mountainMaxHeight-mountainMinHeight)),-1,null);
				}
				if (Math.random() * 100d < valleyPercent) {
					changeTileHeight(face, tile,
							valleyMinHeight + (int) (Math.random()*(valleyMaxHeight-valleyMinHeight)),1,null);
				}
			}
		}
	}

	/**
	 * changeTileHeight is the general method to either lift or sink the tile and it's neighbours in appropriate fashion.
	 * @param face the Face of the Tile tile. needed to find the neighbours
	 * @param tile the Tile that should be adjusted in it's height
	 * @param level the new level the tile should be set to
	 * @param direction -1 denotes a lift of the tile. 1 denotes to sink it. Other values should not be used!
	 * @param lastTile may be null. Needed to that hilltops don't instantly run into themselves
	 */
	private static void changeTileHeight(Face face, Tile tile, int level, int direction, Tile lastTile) {
		if (level>255) level = 255;
		if (level<0)   level = 0;
		if (tile.getHeight()*direction > level*direction) {
			tile.setHeight(level);

			int newLevel = level + (int) ((direction == 1? valleyDeltaHeight : mountainDeltaHeight)
					+ (Math.random()-0.5d)*(direction == 1? valleyDeltaDerivation : mountainDeltaDerivation))*direction;

			Tile[] neighbours = face.getNeighbours(tile.getX(), tile.getY());

			int sameHeightTileId = -1;
			if (Math.random()*100d < (direction == 1? valleyContinuationPercent : mountainContinuationPercent)) {
				while ((sameHeightTileId == -1) || (neighbours[sameHeightTileId] == lastTile)) {
					sameHeightTileId = (int) (Math.random() * 3d);
				}
			}

			for (int i=0; i<3; i++) {
				Tile t = neighbours[i];
				if (t != null) {
					if (i == sameHeightTileId) {
						changeTileHeight(t.getFace(), t, level+direction, direction, tile);
					} else {
						changeTileHeight(t.getFace(), t, newLevel, direction, tile);
					}
				}
			}
		}
	}

	// only temporary?
	public static int getDefaultFluidHeight() {
		return defaultFluidHeight;
	}

}
