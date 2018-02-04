package engine.graphics;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import gui.WorldWindow;

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
//		for (int i=0; i<3; i++) {
//			Mesh side = mesh.getSideMesh(i);
//			if (side == null) break;
//			//this.attachChild(side);
//		}
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
