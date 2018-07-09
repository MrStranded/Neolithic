package renderer;

import org.lwjgl.opengl.GLContext;

public class RenderThread extends Thread {

	private Renderer renderer;
	private int destinationFPS = 60;
	private int millisecondsPerFrame = 1000/destinationFPS;

	public RenderThread(Renderer renderer) {

		this.renderer = renderer;
		renderer.setFps(destinationFPS);
	}

	public RenderThread(Renderer renderer, int fps) {

		this.renderer = renderer;
		setFPS(fps);
		renderer.setFps(destinationFPS);
	}

	private void setFPS(int fps) {

		destinationFPS = fps;
		millisecondsPerFrame = 1000/destinationFPS;
	}

	public void run() {

		renderer.initialize();

		while (renderer.displayExists()) {

			long t = System.currentTimeMillis();

			renderer.render();

			long timeToWait = t + millisecondsPerFrame - System.currentTimeMillis();
			if (timeToWait > 0) {
				try {
					sleep(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		renderer.destroy();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

}
