package engine.window;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.File;

public class Window {

	private String title;
	private int width,height;
	private int fps = 60;

	public Window(int width, int height, String title) {

		this.title = title;
		this.width = width;
		this.height = height;
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {

		setCorrectLWJGLPath();

		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(title);
			Display.create();
		} catch (LWJGLException e) {
			System.err.println("Display wasn't initialized correctly.");
			System.exit(1);
		}
	}

	private void setCorrectLWJGLPath() {
		File JGLLib = null;

		switch(LWJGLUtil.getPlatform()) {
			case LWJGLUtil.PLATFORM_WINDOWS: {
				JGLLib = new File("lib/lwjgl/native/windows/");
			}
			break;

			case LWJGLUtil.PLATFORM_LINUX: {
				JGLLib = new File("lib/lwjgl/native/linux/");
			}
			break;

			case LWJGLUtil.PLATFORM_MACOSX: {
				JGLLib = new File("lib/lwjgl/native/macosx/");
			}
			break;
		}

		if (JGLLib != null) {
			System.setProperty("org.lwjgl.librarypath", JGLLib.getAbsolutePath());
		} else {
			System.out.println("Sadly we do not have the necessary LWJGL libraries for your operating system.");
		}
	}

	// ###################################################################################
	// ################################ Runtime Methods ##################################
	// ###################################################################################

	public boolean isClosed() {
		return Display.isCloseRequested();
	}

	public void update() {
		Display.update();
	}

	public void sync() {
		Display.sync(fps);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void destroy() {
		Display.destroy();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
