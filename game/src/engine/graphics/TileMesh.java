package engine.graphics;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

import java.nio.ByteBuffer;

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

	/**
	 * remembers which array entry in the meshFaces array should be filled by side meshes
	 */
	private int index = 1;

	/**
	 * Creates a mesh for the top triangle from given points.
	 * the first dimension of the array holds the point
	 * the second dimension of the array holds the x,y,z values
	 * @param points coordinates for triangle
	 */
	public void setTopFace(float[][] points) {
		meshFaces[0] = new Mesh();
		meshFaces[0].getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(3));

		final byte[] indices = {0, 1, 2};
		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
		bbuf.put(indices);
		bbuf.rewind();
		meshFaces[0].getMeshData().setIndexBuffer(bbuf);
		meshFaces[0].getMeshData().setIndexMode(IndexMode.Triangles);
		meshFaces[0].getMeshData().getVertexBuffer().clear();

		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				meshFaces[0].getMeshData().getVertexBuffer().put(points[i][j]);
			}
		}

		meshFaces[0].setDefaultColor(new ColorRGBA(1f,1f,1f,1f));
	}

	/**
	 * Creates a mesh for the next side from given points.
	 * the first dimension of the array holds the point
	 * the second dimension of the array holds the x,y,z values
	 * @param points coordinates for triangle
	 */
	public void addSideFace(float[][] points) {
		meshFaces[index] = new Mesh();
		meshFaces[index].getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(4));

		final byte[] indices = {0, 1, 2, 3};
		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
		bbuf.put(indices);
		bbuf.rewind();
		meshFaces[index].getMeshData().setIndexBuffer(bbuf);
		meshFaces[index].getMeshData().setIndexMode(IndexMode.Quads);
		meshFaces[index].getMeshData().getVertexBuffer().clear();

		for (int i=0; i<4; i++) {
			for (int j=0; j<3; j++) {
				meshFaces[index].getMeshData().getVertexBuffer().put(points[i][j]);
			}
		}

		meshFaces[index].setDefaultColor(new ColorRGBA(1f,1f,1f,1f));

		index = (index+1) % 4;
		if (index == 0) index = 1;
	}

	public Mesh getTopMesh() {
		return meshFaces[0];
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
