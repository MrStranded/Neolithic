package engine.graphics;

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael1337 on 07/12/17.
 */
public class WorldMesh  extends Node {

	private ArrayList<TileMesh> meshs;

	public WorldMesh(int numberOfTiles) {
		meshs = new ArrayList<>(numberOfTiles);
	}

	public void registerTile(TileMesh mesh) {
		meshs.add(mesh);
		this.attachChild(mesh.getTopMesh());
		for (int i=0; i<3; i++) {
			Mesh side = mesh.getSideMesh(i);
			if (side == null) break;
			this.attachChild(side);
		}
	}

	public List<Mesh> getMeshes() {
		List<Mesh> meshList = new LinkedList<>();
		for (TileMesh tileMesh : meshs) {
			meshList.add(tileMesh.getTopMesh());
			for (int i=0;i<3;i++) {
				Mesh side = tileMesh.getSideMesh(i);
				if (side==null) break;
				meshList.add(side);
			}
		}
		return meshList;
	}

}
