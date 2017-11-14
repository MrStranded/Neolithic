package engine.graphics;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Tile;

import java.nio.ByteBuffer;

/**
 * Created by Michael on 11.11.2017.
 */
public class MeshGenerator {

	private static WorldRenderer worldRenderer;

	static double anglex = Math.PI/2+0.5d;
	static double angley = Math.PI/2+0.5d;

	static double[] px;
	static double[] py;
	static double[] pz;

	public static void createWorld(Planet planet) {
		if (worldRenderer != null) {
			if (planet != null) {
				for (Face face : planet.getFaces()) {

					int size = face.getSize();

					double[][] coordinates = rotateCoordiantes(face);

					double[] fx = new double[3];
					double[] fy = new double[3];

					double y = 0;

					for (int i=0; i<3; i++) {
						fx[i] = coordinates[i][0] * planet.getRadius();
						fy[i] = coordinates[i][1] * planet.getRadius();
					}

					double v1x = (fx[1]-fx[0])/(double) size;
					double v1y = (fy[1]-fy[0])/(double) size;
					double v2x = (fx[2]-fx[0])/(double) size;
					double v2y = (fy[2]-fy[0])/(double) size;

					px = new double[3];
					py = new double[3];
					pz = new double[3];

					for (int tx = 0; tx < face.getSize(); tx++) {
						for (int ty = 0; ty < face.getSize(); ty++) {
							createTile(face.getTile(tx,ty));
						}
					}
				}
			}
		}
	}
	
	private static void createTile(Tile t) {
		int index = t.getFace().getIndex()*20 + t.getY()*t.getFace().getSize() + t.getX();

		Tile[] neighbours = t.getFace().getNeighbours(t.getX(),t.getY());
		boolean[] lower = new boolean[3];
		int vertices = 3;
		
		for (int i=0; i<3; i++) {
			Tile neighbour = neighbours[i];
			if (neighbour.getHeight() < t.getHeight()) {
				lower[i] = true;
				//vertices += 2;
			} else {
				lower[i] = false;
			}
		}

		Mesh tile = new Mesh();

		tile.getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(vertices));

		// top
		final byte[] indices = new byte[vertices];
		for (byte i=0; i< vertices; i++) { indices[i] = i; }
		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
		bbuf.put(indices);
		bbuf.rewind();
		tile.getMeshData().setIndexBuffer(bbuf);

		tile.getMeshData().setIndexMode(IndexMode.Triangles);

		tile.getMeshData().getVertexBuffer().clear();

		tile.getMeshData().getVertexBuffer().put(-5); // first
		tile.getMeshData().getVertexBuffer().put(0);
		tile.getMeshData().getVertexBuffer().put(0);
		tile.getMeshData().getVertexBuffer().put(5); // second
		tile.getMeshData().getVertexBuffer().put(0);
		tile.getMeshData().getVertexBuffer().put(0);
		tile.getMeshData().getVertexBuffer().put(0); // third
		tile.getMeshData().getVertexBuffer().put(5);
		tile.getMeshData().getVertexBuffer().put(0);

		tile.setDefaultColor(new ColorRGBA(0,1f,0,1f));
	
	}


	/**
	 * Returns rotated coordinates, depending on the two variables anglex and angley.
	 */
	private static double[][] rotateCoordiantes(Face face) {
		double[][] v = face.getCornerCoordinates();
		double[][] nv = new double[3][3];

		for (int i=0; i<3; i++) {
			nv[i][0] = Math.cos(anglex)*v[i][0]
					+ Math.sin(anglex)*Math.cos(angley)*v[i][1]
					+ Math.sin(anglex)*Math.sin(angley)*v[i][2];
			nv[i][1] = -Math.sin(anglex)*v[i][0]
					+ Math.cos(anglex)*Math.cos(angley)*v[i][1]
					+ Math.cos(anglex)*Math.sin(angley)*v[i][2];
			nv[i][2] = -Math.sin(angley)*v[i][1]
					+ Math.cos(angley)*v[i][2];
		}

		return nv;
	}

	// this is being set during the creation of a WorldRenderer object
	public static void setWorldRenderer(WorldRenderer worldRenderer) {
		MeshGenerator.worldRenderer = worldRenderer;
	}
}
