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

		Planet gaia = new Planet(32,10);
		Data.setPlanet(gaia);

		// ------------------- window setup

		boolean mode3d = true;
		DrawThread drawThread;

		if (mode3d) {   // 3D
			WindowInterface window3d = new WorldWindow("Neolihic",1200,1000);

			//drawThread = new DrawThread(window3d,100);
			//drawThread.start();

		} else {        // 2D
			Window window2d = new Window("Neolithic", 1200, 1000);

			DrawPlanet drawPlanet = new DrawPlanet();
			drawPlanet.setPlanet(gaia);
			window2d.assignDrawMethod(drawPlanet);

			drawThread = new DrawThread(window2d, 100);
			drawThread.start();
		}

		// ------------------- geology generation

		PlanetFormer.setPlanet(gaia);
		PlanetFormer planetFormer = new PlanetFormer();

		planetFormer.setDependant(parserThread);
		planetFormer.start();

		new EntityThread(gaia,100).start();
	}
}
