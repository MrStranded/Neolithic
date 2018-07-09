package renderer;

public class RenderThread extends Thread {

	private Renderer renderer;
	private int destinationFPS = 60;
	private int millisecondsPerFrame = 1000/destinationFPS;

	public RenderThread(Renderer renderer) {

		this.renderer = renderer;
	}

	public RenderThread(Renderer renderer, int fps) {

		this.renderer = renderer;
		setFPS(fps);
	}

	private void setFPS(int fps) {

		destinationFPS = fps;
		millisecondsPerFrame = 1000/destinationFPS;
	}

	public void run() {

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
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

}
