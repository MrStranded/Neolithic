package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Michael on 14.06.2017.
 *
 * The class to create a Window in which one may display things.
 */
public class Window implements WindowInterface {

	private JFrame frame;
	private int width, height;

	public Window(String title, int width, int height) {
		frame = new JFrame(title);

		this.width = width;
		this.height = height;

		frame.setBackground(new Color(50,50,50));
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void assignDrawMethod(Draw drawMethod) {
		drawMethod.assignDimensions(this);
		frame.add(drawMethod);
	}

	public Insets getInsets() {
		return frame.getInsets();
	}

	public boolean draw() {
		frame.validate();
		frame.repaint();

		return true;
	}

	public void close() {
		// nothing to do
	}

	public void init() {
		// nothing to do
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}