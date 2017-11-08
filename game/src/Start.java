import data.Data;
import environment.geology.PlanetFormer;
import environment.world.Planet;
import gui.DrawPlanet;
import gui.Window;
import gui.graphics.GraphicsHandler;
import threads.DrawThread;
import threads.EntityThread;
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
		GraphicsHandler.addWindow(window);

		DrawPlanet drawPlanet = new DrawPlanet();
		window.assignDrawMethod(drawPlanet);

		DrawThread drawThread = new DrawThread(100);
		drawThread.start();

		// ------------------- loading

		ParserThread parserThread = new ParserThread();
		parserThread.start();

		// ------------------- planet creation

		Planet gaia = new Planet(32,18);

		drawPlanet.setPlanet(gaia);
		Data.setPlanet(gaia);

		PlanetFormer.setPlanet(gaia);
		PlanetFormer planetFormer = new PlanetFormer();

		planetFormer.setDependant(parserThread);
		planetFormer.start();

		new EntityThread(gaia,100).start();
	}
}
