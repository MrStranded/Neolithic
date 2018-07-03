package main.engine.graphics;

import com.jme3.math.ColorRGBA;
import main.environment.world.Face;
import main.environment.world.Planet;
import main.environment.world.Point;
import main.environment.world.Tile;

/**
 * Created by Michael on 11.11.2017.
 */
public class MeshGenerator {

	static double anglex = Math.PI/2+0.5d;
	static double angley = Math.PI/2+0.5d;

	static double[] px;
	static double[] py;
	static double[] pz;

	public static void createWorld(Planet planet) {
		if (planet != null) {
			WorldMesh worldMesh = new WorldMesh(20);

			for (Face face : planet.getFaces()) {
				Point origin = face.getCorner(0).copy();
				Point dx = face.getCorner(1).subtract(origin).divide(planet.getSize());
				Point dy = face.getCorner(2).subtract(origin).divide(planet.getSize());

				for (int x=0; x<face.getSize(); x++) {
					for (int y=0; y<face.getSize(); y++) {
						Tile tile = face.getTile(x,y);

						Point[] tilePoints = CoordinateCalculator.getPoints(tile);
						Point[] top = CoordinateCalculator.getStretchedPoints(tile,tilePoints);

						TileMesh tileMesh = new TileMesh();
						tileMesh.setTopFace(top[0],top[1],top[2]);

						Point top1,top2,down1,down2;
						double lowerFactor;

						Tile [] neighbours = face.getNeighbours(tile.getX(),tile.getY());
						for (int i = 0; i < 3; i++) {

							for (int n = 0; n <3; n++) {
								if (CoordinateCalculator.hasTwoSharedCorners(tilePoints[i],tilePoints[(i+1)%3],neighbours[n])) {
									lowerFactor = CoordinateCalculator.getHeightFactor(neighbours[n],planet);
									Point[] down = CoordinateCalculator.getStretchedPoints(neighbours[n],tilePoints);

									top1 = top[i];
									top2 = top[(i+1)%3];
									down1 = down[i];
									down2 = down[(i+1)%3];

									ColorRGBA color = new ColorRGBA(0.5f,0.5f,0.5f,1f);
									if (tile.isFlipped()) {
										tileMesh.addSideFace(top1,top2,down1,down2,color);
									} else {
										tileMesh.addSideFace(top2,top1,down2,down1,color);
									}
									break;
								}
							}
						}
						worldMesh.registerTile(tileMesh);
					}
				}
			}

		}
	}

	/**
	 * Clutter?
	 * @param t
	 */
	private static void createTile(Tile t) {
		int index = t.getFace().getIndex()*20 + t.getY()*t.getFace().getSize() + t.getX();

		Tile[] neighbours = t.getFace().getNeighbours(t.getX(),t.getY());
		boolean[] lower = new boolean[3];
		int vertices = 3;

//		for (int i=0; i<3; i++) {
//			Tile neighbour = neighbours[i];
//			if (neighbour.getHeight() < t.getHeight()) {
//				lower[i] = true;
//				//vertices += 2;
//			} else {
//				lower[i] = false;
//			}
//		}

//		Mesh tile = new Mesh();
//
//		tile.getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(vertices));
//
//		// top
//		final byte[] indices = new byte[vertices];
//		for (byte i=0; i< vertices; i++) { indices[i] = i; }
//		final ByteBuffer bbuf = BufferUtils.createByteBuffer(indices.length);
//		bbuf.put(indices);
//		bbuf.rewind();
//		tile.getMeshData().setIndexBuffer(bbuf);
//
//		tile.getMeshData().setIndexMode(IndexMode.Triangles);
//
//		tile.getMeshData().getVertexBuffer().clear();
//
//		tile.getMeshData().getVertexBuffer().put(-5); // first
//		tile.getMeshData().getVertexBuffer().put(0);
//		tile.getMeshData().getVertexBuffer().put(0);
//		tile.getMeshData().getVertexBuffer().put(5); // second
//		tile.getMeshData().getVertexBuffer().put(0);
//		tile.getMeshData().getVertexBuffer().put(0);
//		tile.getMeshData().getVertexBuffer().put(0); // third
//		tile.getMeshData().getVertexBuffer().put(5);
//		tile.getMeshData().getVertexBuffer().put(0);
//
//		tile.setDefaultColor(new ColorRGBA(0,1f,0,1f));
	
	}

	/**
	 * Deprecated?
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
}
