package threads;

/**
 * Created by michael1337 on 26/10/17.
 */
public class DependantThread extends Thread {

	private Thread requiredThread;

	/**
	 * Sets a thread as a required thread for this one. This means this thread waits on the other thread.
	 * @param thread the required thread
	 */
	public void setDependant(Thread thread) {
		requiredThread = thread;
	}

	public void waitForDependantThread() {
		if (requiredThread != null) {
			try {
				requiredThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
