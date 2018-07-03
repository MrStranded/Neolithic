package main;

/**
 * Created by Michael on 14.06.2017.
 *
 * The class with the default start (main) method.
 */
public class Start {

	public static void main (String[] args) {

		// ------------------- loading

		arserThread parserThread = new ParserThread();
		parserThread.start();

		// ------------------- planet creation

		Planet gaia = new Planet(8,15);
		Data.setPlanet(gaia);

		// ------------------- window setup

		int mode = 0; // 0 = 3d, 1 = 2d, 2 = own renderer
		DrawThread drawThread;

		if (mode == 0) {            // 3D
			WindowInterface window3d = new WorldWindow("Neolihic",1200,1000);

			//drawThread = new DrawThread(window3d,100);
			//drawThread.start();

		} else if (mode == 1) {     // 2D
			Window window2d = new Window("Neolithic", 1200, 1000);

			DrawPlanet drawPlanet = new DrawPlanet();
			drawPlanet.setPlanet(gaia);
			window2d.assignDrawMethod(drawPlanet);

			drawThread = new DrawThread(window2d, 100);
			drawThread.start();
		} else if (mode == 2) {     // my own renderer


		}

		// ------------------- geology generation

		PlanetFormer.setPlanet(gaia);
		PlanetFormer planetFormer = new PlanetFormer();

		planetFormer.setDependant(parserThread);
		planetFormer.start();

		new EntityThread(gaia,100).start();
	}
}
