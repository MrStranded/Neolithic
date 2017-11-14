import data.Data;
import environment.geology.PlanetFormer;
import environment.world.Planet;
import gui.DrawPlanet;
import gui.Window;
import gui.WindowInterface;
import gui.WorldWindow;
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

		// ------------------- loading

		ParserThread parserThread = new ParserThread();
		parserThread.start();

		// ------------------- planet creation

		Planet gaia = new Planet(32,18);

		// ------------------- window setup

		WindowInterface window = new WorldWindow(gaia,1200,1000);

		DrawThread drawThread = new DrawThread(window,100);
		drawThread.start();

		// ------------------- geology generation

		Data.setPlanet(gaia);

		PlanetFormer.setPlanet(gaia);
		PlanetFormer planetFormer = new PlanetFormer();

		planetFormer.setDependant(parserThread);
		planetFormer.start();

		new EntityThread(gaia,100).start();
	}
}
