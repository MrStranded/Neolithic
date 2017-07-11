package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Michael on 11.07.2017.
 *
 * Serves as a basic class for all canvases that are needed.
 */
public class Draw extends JPanel implements MouseListener {

	protected int left,top;
	protected int width, height;

	public Draw() {
		this.addMouseListener(this);
	}

	public void assignDimensions(Window window) {
		Insets insets = window.getInsets();
		left = insets.left;
		top = insets.top;

		width = window.getWidth() - insets.left - insets.right;
		height = window.getHeight() - insets.top - insets.bottom;
	}

	// ###################################################################################
	// ################################ Drawing ##########################################
	// ###################################################################################

	public void paintComponent(Graphics g) {

	}

	// ###################################################################################
	// ################################ Mouse ############################################
	// ###################################################################################

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}

}
