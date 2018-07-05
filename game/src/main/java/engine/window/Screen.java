package engine.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Screen extends JPanel implements MouseListener {

	private int width,height;

	public Screen(int width, int height) {

		this.width = width;
		this.height = height;

		addMouseListener(this);
	}

	public void paintComponent(Graphics g) {

		g.drawRect(0,0,200,100);
		g.drawRect(700,400,100,200);
	}

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
