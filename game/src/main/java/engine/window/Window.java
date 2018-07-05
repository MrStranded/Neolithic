package engine.window;

import javax.swing.*;
import java.awt.*;

public class Window {

	private int width,height;
	private String title;

	private JFrame frame;
	private Screen screen;

	private Insets insets;

	public Window(int width, int height, String title) {

		this.width = width;
		this.height = height;
		this.title = title;

		frame = new JFrame(title);

		// get the distance which the frame occupies at the borders
		insets = frame.getInsets();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(width + insets.left + insets.right,height + insets.top + insets.bottom);

		// centering the window
		frame.setLocationRelativeTo(null);

		// adding a drawable surface to the window
		screen = new Screen(width, height);
		frame.add(screen);

		// show the whole thing
		frame.setVisible(true);
	}
}
