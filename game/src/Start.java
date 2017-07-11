import environment.Face;
import environment.Planet;
import environment.geology.PlanetFormer;
import gui.DrawFace;
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
		DrawFace drawFace = new DrawFace();
		window.assignDrawMethod(drawFace);

		DrawThread drawThread = new DrawThread(window,100);
		drawThread.start();

		Planet gaia = new Planet(32,100);
		PlanetFormer.generateTopology(gaia);
		Face faceZero = gaia.getFace(0);
		drawFace.setFace(faceZero);
	}
}
