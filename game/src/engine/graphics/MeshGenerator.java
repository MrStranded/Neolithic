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
							Point p1,p2,p3;
							Tile tile = face.getTile(x,y);

							// calculating normal positions
							p1 = origin.add(dx.multiply(tile.getVX())).add(dy.multiply(tile.getVY()));
							p2 = p1.add(dx);
							p3 = p1.add(dy);

							// when flipped -> translate p1 and swap p2,p3 because of normal of  meshface
							if (tile.isFlipped()) {
								p1 = p1.add(dx).add(dy);
								Point tmp = p2;
								p2 = p3;
								p3 = tmp;
							}

							// multiply by (height of tile/100d + radius) / radius

							double correction = 1d;

							Point[] top = new Point[3];
							double factor = getHeightFactor(tile,planet);
							top[0] = p1.multiply(factor);
							top[1] = p2.multiply(factor);
							top[2] = p3.multiply(factor);

							TileMesh tileMesh = new TileMesh();
							tileMesh.setTopFace(top[0],top[1],top[2]);

							Point[] down = new Point[3];
							Tile [] neighbours = face.getNeighbours(tile.getX(),tile.getY());
							for (int i = 0; i < 3; i++) {
								if (neighbours[i].getHeight() < tile.getHeight()) {
									factor = getHeightFactor(neighbours[i], planet);
									down[i] = p1.multiply(factor);
									down[(i + 1) % 3] = p2.multiply(factor);
									down[(i + 2) % 3] = p3.multiply(factor);
									tileMesh.addSideFace(top[i],top[(i+1)%3],down[i],down[(i+1)%3]);
								}
							}

							worldMesh.registerTile(tileMesh);
						}
					}
				}

				worldRenderer.registerWorldMesh(worldMesh);
			}
		}
	}

	private static double getHeightFactor(Tile tile,Planet planet) {
		double correction = 100d;
		return ((((double) tile.getHeight())/correction + planet.getRadius()) / planet.getRadius());
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
