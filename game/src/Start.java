import environment.world.Face;
import environment.world.Planet;
import environment.geology.PlanetFormer;
import gui.DrawFace;
import gui.DrawPlanet;
import gui.Window;
import threads.DrawThread;

/**
 * Created by Michael on 14.06.2017.
 *
 * The class with the default start (main) method.
 */
public class Start {

	public static void main (String[] args) {

		Window window = new Window("Neolithic",1200,1000);
		DrawPlanet drawPlanet = new DrawPlanet();
		window.assignDrawMethod(drawPlanet);

		DrawThread drawThread = new DrawThread(window,50);
		drawThread.start();

		Planet gaia = new Planet(32,18);
		PlanetFormer.generateTopology(gaia);
		PlanetFormer.generateTrees(gaia);
		//WeatherCreater.generateWeather(gaia);

		drawPlanet.setPlanet(gaia);

		//MeteorologyThread meteorologyThread = new MeteorologyThread(gaia,100);
		//meteorologyThread.start();
	}
}
