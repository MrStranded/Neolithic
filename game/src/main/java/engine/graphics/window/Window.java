package engine.graphics.window;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import engine.graphics.renderer.Renderer;

import java.nio.IntBuffer;

public class Window {

	private String title;
	private int width,height;

	private Renderer renderer;
	private long window;

	public Window(int width, int height, String title) {
		this.title = title;
		this.width = width;
		this.height = height;
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {
		createWindow();
		createKeyCallback();
		createResizeCallback();
		registerWindow();
	}

	private void createWindow() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable

		// Create the window
		window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (window == 0) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
	}

	/**
	 * This method is disfunctional, as soon as a KeyboardInput is created, which references this window.
	 */
	private void createKeyCallback() {
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			}
		});
	}

	private void createResizeCallback() {
		// Setup a resize callback. It will be called every time the window is resized.
		Window self = this;
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				self.width = width;
				self.height = height;
				GL11.glViewport(0, 0, width, height);
				if (renderer != null) {
					renderer.calculateProjectionMatrix();
				}
			}
		});
	}

	private void registerWindow() {
		// Get the thread stack and push a new frame
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

			// Center the window
			GLFW.glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(window);
		// Enable v-sync
		GLFW.glfwSwapInterval(1);

		// Make the window visible
		GLFW.glfwShowWindow(window);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// enable back face culling and depth testing
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		// wireframe on: use GL11.GL_LINE, wireframe off: use GL11.GL_FILL
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		// Set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Support for transparencies
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	// ###################################################################################
	// ################################ Runtime Methods ##################################
	// ###################################################################################

	public void close() {
		GLFW.glfwSetWindowShouldClose(window,true);
	}

	public boolean isClosed() {
		return GLFW.glfwWindowShouldClose(window);
	}

	public void flip() {

		GLFW.glfwSwapBuffers(window); // swap the color buffers

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		GLFW.glfwPollEvents();
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void destroy() {
		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public long getWindowId() {
		return window;
	}
}
