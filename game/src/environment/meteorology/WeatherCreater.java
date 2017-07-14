package environment.meteorology;

import environment.Face;
import environment.Planet;

/**
 * Created by Michael on 14.07.2017.
 *
 * This class generates the weather of the world.
 */
public class WeatherCreater {

	public static void generateWeather(Planet planet) {
		if ((planet != null)&&(planet.getFaces() != null)) {
			for (Face face : planet.getFaces()) {
				generateFaceWeather(face);
			}
		}
	}

	private static void generateFaceWeather(Face face) {
		int size = face.getSize();
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (Math.random()>0.75d) {
					face.getTile(x,y).setHumidity(100);
				}
			}
		}
	}

}
