package engine.logic;

import engine.data.Planet;
import engine.data.Tile;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.objects.planet.PlanetObject;
import engine.math.numericalObjects.Vector3;

public class GeographicCoordinates {

	/**
	 * Calculates the corresponding Tile to geographic coordinates given as longitude and latitude in degrees.
	 * @param planet to search
	 * @param longitude (x) in degrees
	 * @param latitude (y) in degrees
	 * @return Tile with given coordinates
	 */
	public static Tile getTile(Planet planet, double longitude, double latitude) {
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

			if ((closest == null) || (value > closestValue)) {
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

	public static double getLongitude(Tile tile) {
		FacePart facePart = tile.getTileMesh();

		double yaw = -2d * Math.atan2(facePart.getNormal().getY(), facePart.getNormal().getX());

		return Math.toDegrees(yaw);
	}

}
