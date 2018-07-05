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

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void paintComponent(Graphics g) {

		System.out.println("painting "+width+" , "+height);

		int n = 4;
		for (int i=0; i<n; i++) {
			g.drawRect(i*width/n,100,width/n, height/2);
		}
		//g.drawRect(0,0,width/4,height/8);
		//g.drawRect(width*3/4,height*7/8,width/4-1,height/8-1);
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
