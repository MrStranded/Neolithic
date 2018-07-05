package main.engine.graphics;

import com.jme3.scene.Geometry;
import main.gui.WorldWindow;

import java.util.ArrayList;

/**
 * Created by michael1337 on 07/12/17.
 */
public class WorldMesh {

	private ArrayList<TileMesh> meshs;

	public WorldMesh(int numberOfTiles) {
		meshs = new ArrayList<>(numberOfTiles);
	}

	public void registerTile(TileMesh mesh) {
		meshs.add(mesh);
		WorldWindow.getStaticRootNode().attachChild(mesh.getTopGeometry());
		for (int i=0; i<3; i++) {
			Geometry side = mesh.getSideGeometry(i);
			if (side == null) break;
			WorldWindow.getStaticRootNode().attachChild(side);
		}
	}
//
//	public List<Mesh> getMeshes() {
//		List<Mesh> meshList = new LinkedList<>();
//		for (TileMesh tileMesh : meshs) {
//			meshList.add(tileMesh.getTopMesh());
//			for (int i=0;i<3;i++) {
//				Mesh side = tileMesh.getSideMesh(i);
//				if (side==null) break;
//				meshList.add(side);
//			}
//		}
//		return meshList;
//	}

}