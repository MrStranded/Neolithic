package environment.meteorology;

import environment.Face;
import environment.Planet;

import java.util.Iterator;

/**
 * Created by Michael on 14.07.2017.
 *
 * This class creates raindrops on humid tiles.
 */
public class Rain {

	public static void letItRain(Planet planet) {
		if ((planet != null)&&(planet.getFaces() != null)) {
			for (Face face : planet.getFaces()) {
				letRainFall(face);
				generateFaceWeather(face);
			}
		}
	}

	private static void generateFaceWeather(Face face) {
		int size = face.getSize();
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (face.getTile(x,y).getHumidity()>=100) {
					if (Math.random()>0.75d) face.getTile(x,y).createRainDrop();
				}
			}
		}
	}

	private static void letRainFall(Face face) {
		int size = face.getSize();
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (face.getTile(x,y).getRain()!=null) {
					Iterator<RainDrop> rainDropIterator = face.getTile(x,y).getRain().iterator();
					while (rainDropIterator.hasNext()) {
						RainDrop rainDrop = rainDropIterator.next();
						rainDrop.setHeight(rainDrop.getHeight()-2);
						if (rainDrop.getHeight()<face.getTile(x,y).getHeight()) {
							rainDropIterator.remove();
						}
					}
				}
			}
		}
	}

}
