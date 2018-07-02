package main.environment.geology;

import main.data.Data;
import main.data.proto.Container;
import main.data.proto.Value;
import main.engine.EntityBuilder;
import main.engine.graphics.MeshGenerator;
import main.environment.world.Entity;
import main.environment.world.Face;
import main.environment.world.Planet;
import main.environment.world.Tile;
import main.threads.DependantThread;

import java.util.ArrayList;

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

	private static ArrayList<Formation> formations = new ArrayList<>();

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

		generateMesh();
		System.out.println("Mesh generation completed.");
	}

	private static void initializeGenerationValues() {
		if (planet != null) size = planet.getFace(0).getSize();

		Container worldGen = Data.getContainer(Data.getContainerId("gen01"));
		if (worldGen != null) {
			defaultTileId = Data.getContainerId(worldGen.getString("defaultTile"));
			defaultTileHeight = worldGen.getInt("defaultTileHeight");

			defaultFluidId = Data.getContainerId(worldGen.getString("defaultFluid"));
			defaultFluidHeight = worldGen.getInt("defaultFluidHeight");

			Value formationsValue = worldGen.tryToGet("formations");
			if (formationsValue != null) {
				for (String formationTextId : formationsValue.getData()) {
					Container formation = Data.getContainer(Data.getContainerId(formationTextId));
					if (formation != null) {
						formations.add(new Formation(formation));
					}
				}
			}
		}
	}

	public static void generateMesh() {
		MeshGenerator.createWorld(planet);
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

				for (Formation formation : formations) {
					if (Math.random() * 100d < formation.getSpawnPercent()) {
						int h = formation.getMinHeight() + (int) (Math.random()*(formation.getMaxHeight()-formation.getMinHeight()));
						int d = h>tile.getHeight()? -1 : 1;
						changeTileHeight(face,tile,formation,h,d,null);
					}
				}
			}
		}
	}

	/**
	 * changeTileHeight is the general method to either lift or sink the tile and it's neighbours in appropriate fashion.
	 * @param face the Face of the Tile tile. needed to find the neighbours
	 * @param tile the Tile that should be adjusted in it's height
	 * @param formation  the Formation that the geology data is taken from
	 * @param level the new level the tile should be set to
	 * @param direction -1 denotes a lift of the tile. 1 denotes to sink it. Other values should not be used!
	 * @param lastTile may be null. Needed to that hilltops don't instantly run into themselves
	 */
	private static void changeTileHeight(Face face, Tile tile, Formation formation, int level, int direction, Tile lastTile) {
		if (level>255) level = 255;
		if (level<0)   level = 0;
		if (tile.getHeight()*direction > level*direction) {
			tile.setHeight(level);

			int newLevel = level + (int) (formation.getDeltaHeight() + (Math.random()-0.5d)*formation.getDeltaDerivation())*direction;

			Tile[] neighbours = face.getNeighbours(tile.getX(), tile.getY());

			int sameHeightTileId = -1;
			if (Math.random()*100d < formation.getContinuationPercent()) {
				while ((sameHeightTileId == -1) || (neighbours[sameHeightTileId] == lastTile)) {
					sameHeightTileId = (int) (Math.random() * 3d);
				}
			}

			for (int i=0; i<3; i++) {
				Tile t = neighbours[i];
				if (t != null) {
					if (i == sameHeightTileId) {
						changeTileHeight(t.getFace(), t, formation,level+direction, direction, tile);
					} else {
						changeTileHeight(t.getFace(), t, formation, newLevel, direction, tile);
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
