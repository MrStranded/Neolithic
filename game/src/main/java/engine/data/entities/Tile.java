package engine.data.entities;

import constants.TopologyConstants;
import engine.data.Data;
import engine.data.IDInterface;
import engine.data.planetary.Face;
import engine.data.proto.TileContainer;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.renderer.color.RGBA;
import engine.logic.topology.Neighbour;

public class Tile extends Instance implements IDInterface {

	private int xPos,yPos;
	private int height = 0;
	private int waterHeight = TopologyConstants.PLANET_OCEAN_HEIGHT;

	private Face face;
	private FacePart tileMesh;

	private RGBA topColor = null;
	private RGBA sideColor = null;

	public Tile(int id, int xPos, int yPos, Face face) {
		super(id);
		Data.addInstanceToQueue(this);

		this.xPos = xPos;
		this.yPos = yPos;
		this.face = face;
	}

	public Tile(int id, Tile old) {
		super(id);
		Data.addInstanceToQueue(this);

		this.xPos = old.getX();
		this.yPos = old.getY();
		this.height = old.getHeight();
		this.waterHeight = old.getWaterHeight();

		this.face = old.getFace();
		this.tileMesh = old.getTileMesh();
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return this;
	}

	public void resetColors() {
		topColor = null;
		sideColor = null;
	}

	private void createRandomizedColor() {
		TileContainer protoTile = (TileContainer) Data.getContainer(id).orElse(null);

		if (protoTile != null) {
			RGBA protoTopColor = protoTile.getTopColor();
			RGBA protoTopColorDeviation = protoTile.getTopColorDeviation();

			topColor = new RGBA(
					protoTopColor.getR() + protoTopColorDeviation.getR()*(Math.random()*2d - 1d),
					protoTopColor.getG() + protoTopColorDeviation.getG()*(Math.random()*2d - 1d),
					protoTopColor.getB() + protoTopColorDeviation.getB()*(Math.random()*2d - 1d)
			);

			RGBA protoSideColor = protoTile.getSideColor();
			RGBA protoSideColorDeviation = protoTile.getSideColorDeviation();

			sideColor = new RGBA(
					protoSideColor.getR() + protoSideColorDeviation.getR()*(Math.random()*2d - 1d),
					protoSideColor.getG() + protoSideColorDeviation.getG()*(Math.random()*2d - 1d),
					protoSideColor.getB() + protoSideColorDeviation.getB()*(Math.random()*2d - 1d)
			);
		} else {
			topColor = TopologyConstants.TILE_DEFAULT_COLOR;
			sideColor = topColor.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	/**
	 * Slot 0 is in the middle of the tile. Slots 1 to 3 are close to corners 1 to 3.
	 * @return free slot nr ranging from 0 to 3
	 */
	public int getOpenSlot(Instance instance) {
		if (getSubInstances() == null || getSubInstances().isEmpty()) { return 0; }

		int slot = 0;
		for (Instance sub : getSubInstances()) {
			if (sub == instance) { return slot; }
			if (sub.hasMesh()) { slot++; }
		}
		return slot % 4;
	}

	@Override
	public Tile getPosition() {
		return this;
	}

	public boolean canGo(Tile from, Tile to) { return true; }

	public Face getFace() {
		return face;
	}

	public FacePart getTileMesh() {
		return tileMesh;
	}
	public void setTileMesh(FacePart tileMesh) {
		this.tileMesh = tileMesh;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		boolean hasChanged = (this.height != height);
		if (hasChanged) {
			this.height = height;
			setChanged(true);
			for (Tile neighbour : Neighbour.getNeighbours(this)) {
				if (neighbour != null) {
					neighbour.setChanged(true);
				}
			}
			actualizeObjectPositions();
		}
	}

	public int getWaterHeight() {
		return waterHeight;
	}
	public void setWaterHeight(int waterHeight) {
		boolean hasChanged = (this.waterHeight != waterHeight);
		if (hasChanged) {
			this.waterHeight = waterHeight;
			setChanged(true);
			for (Tile neighbour : Neighbour.getNeighbours(this)) {
				if (neighbour != null) {
					neighbour.setChanged(true);
				}
			}
			actualizeObjectPositions();
		}
	}

	private void actualizeObjectPositions() {
		if (getSubInstances() != null) {
			for (Instance instance : getSubInstances()) {
				instance.actualizeObjectPosition();
			}
		}
	}

	public void setChanged(boolean changed) {
		if (tileMesh != null) {
			tileMesh.setChanged(changed);
		}

		// when tile has been updated
		if (! changed) {
			actualizeObjectPositions();
		}
	}

	public RGBA getTopColor() {
		if (topColor == null) {
			createRandomizedColor();
		}
		return topColor;
	}

	public RGBA getSideColor() {
		if (sideColor == null) {
			createRandomizedColor();
		}
		return sideColor;
	}

	public String toString() {
		return "Tile (face = " + face.getX() + ", " + face.getY() + ", tile = " + getX() + ", " + getY() + ")";
	}

}
