package engine.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Window {

	private String title;

	private JFrame frame;
	private Screen screen;

	public Window(int width, int height, String title) {

		initialize(width, height, title);
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	private void initialize(int width, int height, String title) {

		this.title = title;

		frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		screen = new Screen(width,height);

		frame.getContentPane().add(screen);
		frame.pack();

		// centering the window
		frame.setLocationRelativeTo(null);

		addResizeListener();

		// switching to active rendering
		frame.setIgnoreRepaint(true);
		screen.setIgnoreRepaint(true);
		// creating double buffering
		screen.createBufferStrategy(2);

		frame.setVisible(true);
	}

	private void addResizeListener() {

		// a trick that seems to work
		Window window = this;

		// checking the resizing of the window and behave accordingly
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				screen.setSize(frame.getWidth(), frame.getHeight());
			}
		});
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Screen getScreen() {
		return screen;
	}
}
