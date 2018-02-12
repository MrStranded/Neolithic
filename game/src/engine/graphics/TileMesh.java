package engine.graphics;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import environment.world.Point;
import gui.WorldWindow;

/**
 * Handles the lower level generation of meshes for single tiles.
 * Each tile should get a top face, but side faces should only exist on the borders to lower tiles.
 *
 * Created by michael1337 on 07/12/17.
 */
public class TileMesh {

	/**
	 * 0 -> upper face
	 * 1-3 -> optional side faces
	 */
	private Mesh[] meshFaces = new Mesh[4];
	private Geometry[] meshGeometries = new Geometry[4];

	/**
	 * remembers which array entry in the meshFaces array should be filled by side meshes
	 */
	private int index = 1;

	/**
	 * Creates a mesh for the top triangle from given points.
	 * the first dimension of the array holds the point
	 * the second dimension of the array holds the x,y,z values
	 */
	public void setTopFace(Point p1, Point p2, Point p3) {
		meshFaces[0] = new Mesh();

		short[] indexes = {0, 2, 1};

		// Vertex positions in space
		Vector3f[] vertices = new Vector3f[3];
		vertices[0] = new Vector3f(p1.getXf(),p1.getYf(),p1.getZf());
		vertices[1] = new Vector3f(p2.getXf(),p2.getYf(),p2.getZf());
		vertices[2] = new Vector3f(p3.getXf(),p3.getYf(),p3.getZf());

		// Texture coordinates
		Vector2f[] texCoord = new Vector2f[4];
		texCoord[0] = new Vector2f(0,0);
		texCoord[1] = new Vector2f(1,0);
		texCoord[2] = new Vector2f(0,1);

		// Setting buffers
		meshFaces[0].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		meshFaces[0].setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		meshFaces[0].setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
		meshFaces[0].updateBound();

		// Creating a geometry, and apply a single color material to it
		meshGeometries[0] = new Geometry("Tile_Top", meshFaces[0]);
		Material mat = new Material(WorldWindow.getStaticAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", new ColorRGBA(0.8f,0.7f,0.1f,1f));
		meshGeometries[0].setMaterial(mat);
	}

	/**
	 * Creates a mesh for the next side from given points.
	 * the first dimension of the array holds the point
	 * the second dimension of the array holds the x,y,z values
	 */
	public void addSideFace(Point top1, Point top2, Point down1, Point down2) {
		meshFaces[index] = new Mesh();

		short[] indexes = {0, 1, 3, 2};

		// Vertex positions in space
		Vector3f[] vertices = new Vector3f[4];
		vertices[0] = new Vector3f(top1.getXf(),top1.getYf(),top1.getZf());
		vertices[1] = new Vector3f(top2.getXf(),top2.getYf(),top2.getZf());
		vertices[2] = new Vector3f(down1.getXf(),down1.getYf(),down1.getZf());
		vertices[3] = new Vector3f(down2.getXf(),down2.getYf(),down2.getZf());

		// Texture coordinates
		Vector2f[] texCoord = new Vector2f[4];
		texCoord[0] = new Vector2f(0,0);
		texCoord[1] = new Vector2f(1,0);
		texCoord[2] = new Vector2f(0,1);
		texCoord[3] = new Vector2f(1,1);

		// Setting buffers
		meshFaces[index].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		meshFaces[index].setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		meshFaces[index].setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
		meshFaces[index].updateBound();

		// Creating a geometry, and apply a single color material to it
		meshGeometries[index] = new Geometry("Tile_Side", meshFaces[index]);
		Material mat = new Material(WorldWindow.getStaticAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", new ColorRGBA(0.6f,0.5f,0.0f,0.7f));
		meshGeometries[index].setMaterial(mat);

		index = (index+1) % 4;
		if (index == 0) index = 1;
	}

	public Mesh getTopMesh() {
		return meshFaces[0];
	}
	public Geometry getTopGeometry() {
		return meshGeometries[0];
	}

	/**
	 * i reaches from 0 to 2, other than the technical implementation inside the class
	 * @param i side from 0-2
	 * @return mesh of the side. may be null!
	 */
	public Mesh getSideMesh(int i) {
		if ((i < 0) || (i > 2)) return null;
		return meshFaces[i+1];
	}
	public Geometry getSideGeometry(int i) {
		if ((i < 0) || (i > 2)) return null;
		return meshGeometries[i+1];
	}

}
