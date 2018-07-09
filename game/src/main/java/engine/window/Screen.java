package engine.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Screen extends Canvas implements MouseListener {

	public Screen(int width, int height) {

		setSize(width,height);

		addMouseListener(this);
	}

	// ###################################################################################
	// ################################ Screen Control ###################################
	// ###################################################################################

	/**
	 * We don't call the super.setSize method here, because then the borders would change the actual size of the window.
	 * Calling setPreferredSize ensures that the screen has exactly the size we want.
	 * @param width
	 * @param height
	 */
	@Override
	public void setSize(int width, int height) {
		setPreferredSize(new Dimension(width,height));
	}

	// ###################################################################################
	// ################################ Drawing ##########################################
	// ###################################################################################

	public void drawTestImage(Graphics g) {

		int w = getWidth();
		int h = getHeight();

		System.out.println("painting " + w + " , " + h);

		int n = 4;
		for (int i=0; i<n; i++) {
			g.drawRect(i*w/n,h/4,w/n, h/2);
		}

		g.drawOval(0,0,w,h);
	}

	// ###################################################################################
	// ################################ Mouse Control ####################################
	// ###################################################################################

	@Override
	public void mouseClicked(MouseEvent e) {

		System.out.println("clicked");
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
