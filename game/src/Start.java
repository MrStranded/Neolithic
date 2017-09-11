import environment.world.Face;
import environment.world.Planet;
import environment.geology.PlanetFormer;
import gui.DrawFace;
import gui.DrawPlanet;
import gui.Window;
import parser.Parser;
import threads.DrawThread;
import threads.ParserThread;

/**
 * Created by Michael on 14.06.2017.
 *
 * The class with the default start (main) method.
 */
public class Start {

	public static void main (String[] args) {

		// ------------------- window setup

		Window window = new Window("Neolithic",1200,1000);
		DrawPlanet drawPlanet = new DrawPlanet();
		window.assignDrawMethod(drawPlanet);

		DrawThread drawThread = new DrawThread(window,42);
		drawThread.start();

		// ------------------- loading

		ParserThread parserThread = new ParserThread();
		parserThread.start();

		// ------------------- planet creation

		Planet gaia = new Planet(32,18);

		drawPlanet.setPlanet(gaia);

		PlanetFormer.setPlanet(gaia);
		new PlanetFormer().start();

		//WeatherCreater.generateWeather(gaia);
		//MeteorologyThread meteorologyThread = new MeteorologyThread(gaia,100);
		//meteorologyThread.start();
	}
}
