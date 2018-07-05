package engine.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

		// a trick that seems to work
		Window window = this;

		// checking the resizing of the window and behave accordingly
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				System.out.println("resized!");

				window.width = frame.getWidth();
				window.height = frame.getHeight();

				screen.setSize(window.width - insets.left - insets.right, window.height - insets.top - insets.bottom);

				frame.repaint();
			}
		});

		// everything is ready - show the whole thing
		frame.setVisible(true);
	}
}
