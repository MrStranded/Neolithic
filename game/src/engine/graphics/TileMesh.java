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
//	public void addSideFace(Point top1, Point top2, Point down1, Point down2) {
//		meshFaces[index] = new Mesh();
//		meshFaces[index].getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(4));
//
//		final byte[] indices = {0, 1, 2, 3};
//		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
//		bbuf.put(indices);
//		bbuf.rewind();
//		meshFaces[index].getMeshData().setIndexBuffer(bbuf);
//		meshFaces[index].getMeshData().setIndexMode(IndexMode.Quads);
//		meshFaces[index].getMeshData().getVertexBuffer().clear();
//
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top1.getX());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top1.getY());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top1.getZ());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top2.getX());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top2.getY());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) top2.getZ());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down2.getX());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down2.getY());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down2.getZ());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down1.getX());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down1.getY());
//		meshFaces[index].getMeshData().getVertexBuffer().put((float) down1.getZ());
//
//		meshFaces[index].setDefaultColor(new ColorRGBA(1f,1f,1f,1f));
//
//		index = (index+1) % 4;
//		if (index == 0) index = 1;
//	}

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

}
