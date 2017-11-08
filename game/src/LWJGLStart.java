/**
 * A class to test the use of LWJGL / Vulkan
 *
 * Created by michael1337 on 06/11/17.
 */

import gui.graphics.GraphicsHandler;
import gui.graphics.Window3D;
import threads.DrawThread;

public class LWJGLStart {

	public static void main(String[] args) {
		GraphicsHandler.addWindow(new Window3D(800,600));

		DrawThread drawThread = new DrawThread(100);
		drawThread.start();

		try {
			drawThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
