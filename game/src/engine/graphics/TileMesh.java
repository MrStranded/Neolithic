package engine.graphics;

/**
 * Handles the lower level generation of meshes for single tiles.
 * Each tile should get a top face, but side faces should only exist on the borders to lower tiles.
 *
 * Created by michael1337 on 07/12/17.
 */
public class TileMesh {

//	/**
//	 * 0 -> upper face
//	 * 1-3 -> optional side faces
//	 */
//	private Mesh[] meshFaces = new Mesh[4];
//
//	/**
//	 * remembers which array entry in the meshFaces array should be filled by side meshes
//	 */
//	private int index = 1;
//
//	/**
//	 * Creates a mesh for the top triangle from given points.
//	 * the first dimension of the array holds the point
//	 * the second dimension of the array holds the x,y,z values
//	 */
//	public void setTopFace(Point p1, Point p2, Point p3) {
//		meshFaces[0] = new Mesh();
//		meshFaces[0].getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(3));
//
//		final byte[] indices = {0, 2, 1};
//		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
//		bbuf.put(indices);
//		bbuf.rewind();
//		meshFaces[0].getMeshData().setIndexBuffer(bbuf);
//		meshFaces[0].getMeshData().setIndexMode(IndexMode.Triangles);
//		meshFaces[0].getMeshData().getVertexBuffer().clear();
//
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p1.getX());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p1.getY());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p1.getZ());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p2.getX());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p2.getY());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p2.getZ());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p3.getX());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p3.getY());
//		meshFaces[0].getMeshData().getVertexBuffer().put((float) p3.getZ());
//
//		meshFaces[0].setDefaultColor(new ColorRGBA(1f,1f,1f,1f));
//	}
//
//	/**
//	 * Creates a mesh for the next side from given points.
//	 * the first dimension of the array holds the point
//	 * the second dimension of the array holds the x,y,z values
//	 */
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
//
//	public Mesh getTopMesh() {
//		return meshFaces[0];
//	}
//
//	/**
//	 * i reaches from 0 to 2, other than the technical implementation inside the class
//	 * @param i side from 0-2
//	 * @return mesh of the side. may be null!
//	 */
//	public Mesh getSideMesh(int i) {
//		if ((i < 0) || (i > 2)) return null;
//		return meshFaces[i+1];
//	}

}
