package engine.graphics;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Point;
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
				WorldMesh worldMesh = new WorldMesh(20);

				for (Face face : planet.getFaces()) {
					Point origin = face.getCorner(0).copy();
					Point dx = face.getCorner(1).subtract(origin).divide(planet.getSize());
					Point dy = face.getCorner(2).subtract(origin).divide(planet.getSize());

					for (int x=0; x<face.getSize(); x++) {
						for (int y=0; y<face.getSize(); y++) {
							Point p1 = origin.add(dx.multiply(x)).add(dy.multiply(y));
							Point p2 = p1.add(dx);
							Point p3 = p1.add(dy);

							TileMesh tileMesh = new TileMesh();
							tileMesh.setTopFace(p1,p2,p3);
							worldMesh.registerTile(tileMesh);
						}
					}
				}

				worldRenderer.registerWorldMesh(worldMesh);
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
