package engine.graphics;

import environment.world.Face;
import environment.world.Planet;
import environment.world.Point;
import environment.world.Tile;

/**
 * This class bundles methods for calculating Tile and Face related coordinate calculations.
 * They are collected here, so as not to clutter the Face or Tile classes.
 *
 * Created by michael1337 on 22/03/18.
 */
public class CoordinateCalculator {

	private static final double epsilon = 1d/1024d; // used to decide whether two points lie on the same location

	/**
	 * Calculates whether the asked tile has two corners that lay approximately on the two given points.
	 * @param t1Point1
	 * @param t1Point2
	 * @param t2
	 * @return true when they share exactly two corners
	 */
	public static boolean hasTwoSharedCorners(Point t1Point1, Point t1Point2, Tile t2) {
		Point[] t2Points = getPoints(t2);
		int sameCorners = 0;

		// we have to check the two points of our tile with every point of the other tile
		for (int i=0; i<3; i++) {
			double delta;

			// checking with the first point
			delta = 0;
			delta += Math.abs(t1Point1.getX() - t2Points[i].getX());
			delta += Math.abs(t1Point1.getY() - t2Points[i].getY());
			delta += Math.abs(t1Point1.getZ() - t2Points[i].getZ());
			if (delta < epsilon) { sameCorners++; }

			// checking with the second point
			delta = 0;
			delta += Math.abs(t1Point2.getX() - t2Points[i].getX());
			delta += Math.abs(t1Point2.getY() - t2Points[i].getY());
			delta += Math.abs(t1Point2.getZ() - t2Points[i].getZ());
			if (delta < epsilon) { sameCorners++; }
		}

		return (sameCorners == 2);
	}

	/**
	 * Calculates the 3d Points of the Tile in the Universe.
	 * Attention: Those Points are not scaled to account for tile height!
	 * Attention: Those Points are not rotated or moved in any way according to planet movement!
	 * @param tile
	 * @return Point array of size 3
	 */
	public static Point[] getPoints(Tile tile) {
		Face face = tile.getFace();
		Point[] p = new Point[3];

		Point origin = face.getCorner(0).copy();
		Point dx = face.getCorner(1).subtract(origin).divide(face.getSize());
		Point dy = face.getCorner(2).subtract(origin).divide(face.getSize());

		// calculating normal positions
		p[0] = origin.add(dx.multiply(tile.getVX())).add(dy.multiply(tile.getVY()));
		p[1] = p[0].add(dx);
		p[2] = p[0].add(dy);

		// when flipped -> translate p0 and swap p1,p2 because of normal of mesh face
		if (tile.isFlipped()) {
			p[0] = p[0].add(dx).add(dy);
			Point tmp = p[1];
			p[1] = p[2];
			p[2] = tmp;
		}

		return p;
	}

	/**
	 * Returns the correctly stretched tile points (that you can get with CoordinateCalculator.getPoints(Tile)).
	 * The stretching factor is determined from the height of the given tile.
	 * @param tile determines stretiching factor
	 * @param tilePoints this point array will be copied and stretched
	 * @return Point array of size 3
	 */
	public static Point[] getStretchedPoints(Tile tile, Point[] tilePoints) {
		Point[] top = new Point[3];
		double factor = getHeightFactor(tile,tile.getFace().getPlanet());

		for (int i=0; i<3; i++) { top[i] = tilePoints[i].multiply(factor); }
		return top;
	}

	/**
	 * Returns the coordinate stretching factor for the given tile.
	 * The calculation is based on the radius of the planet and a planet specific correction value.
	 * The correction value normalizes the Tile heights.
	 * @param tile
	 * @param planet
	 * @return stretching factor planet.radius <= factor
	 */
	public static double getHeightFactor(Tile tile,Planet planet) {
		return ((double) tile.getHeight()/planet.getTileHeightCorrection()) + planet.getRadius();
	}

}
