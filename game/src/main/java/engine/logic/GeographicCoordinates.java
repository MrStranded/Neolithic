package engine.logic;

import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.objects.planet.PlanetObject;
import engine.math.numericalObjects.Vector3;

public interface GeographicCoordinates {

	/**
	 * Calculates the corresponding Tile to geographic coordinates given as longitude and latitude in degrees.
	 * @param planet to search
	 * @param longitude (x) in degrees
	 * @param latitude (y) in degrees
	 * @return Tile with given coordinates
	 */
	static Tile getTile(Planet planet, double longitude, double latitude) {
		double yaw = Math.toRadians(longitude);
		double pitch = -Math.toRadians(latitude); // minus because i want positive pitches to go north

		double heighFactor = Math.cos(pitch);
		double x = Math.cos(yaw) * heighFactor;
		double y = Math.sin(pitch);
		double z = Math.sin(yaw) * heighFactor;

		Vector3 search = new Vector3(x, y, z);

		PlanetObject planetObject = planet.getPlanetObject();
		FacePart closest = null;
		double closestValue = 0d;

		for (FacePart facePart : planetObject.getFaceParts()) {
			double value = search.dot(facePart.getNormal());

			if (value > closestValue) {
				closest = facePart;
				closestValue = value;
			}
		}

		while (closest.getQuarterFaces() != null && closest.getQuarterFaces().length > 0) {
			closestValue = 0d;

			for (FacePart facePart : closest.getQuarterFaces()) {
				double value = search.dot(facePart.getNormal());

				if (value > closestValue) {
					closest = facePart;
					closestValue = value;
				}
			}
		}

		return closest.getTile();
	}

	/**
	 * Returns the longitude of the given tile in degrees.
	 * The longitude corresponds to the rotation around the y axis or the yaw.
	 * @param tile
	 * @return
	 */
	static double getLongitudeDegrees(Tile tile) {
		return Math.toDegrees(getLongitude(tile));
	}

	/**
	 * Returns the latitude of the given tile in degrees.
	 * The latitude corresponds to the rotation around the x axis or the pitch.
	 * @param tile
	 * @return
	 */
	static double getLatitudeDegrees(Tile tile) {
		return Math.toDegrees(getLatitude(tile));
	}
	/**
	 * Returns the longitude of the given tile in radian.
	 * The longitude corresponds to the rotation around the y axis or the yaw.
	 * @param tile
	 * @return
	 */
	static double getLongitude(Tile tile) {
		FacePart facePart = tile.getTileMesh();

		double x = facePart.getNormal().getX();
		double z = facePart.getNormal().getZ();

		return Math.atan2(z, x);
	}

	/**
	 * Returns the latitude of the given tile in radian.
	 * The latitude corresponds to the rotation around the x axis or the pitch.
	 * @param tile
	 * @return
	 */
	static double getLatitude(Tile tile) {
		FacePart facePart = tile.getTileMesh();

		double x = facePart.getNormal().getX();
		double y = facePart.getNormal().getY();
		double z = facePart.getNormal().getZ();

		return -Math.atan2(y, Math.sqrt(x*x + z*z));
	}

}
