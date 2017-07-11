package gui;

import environment.Face;
import environment.Tile;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by Michael on 11.07.2017.
 *
 * A class to draw map faces.
 */
public class DrawFace extends Draw {

	private Face face;
	private int mx=400,my=20;

	// ###################################################################################
	// ################################ Drawing ##########################################
	// ###################################################################################

	public void paintComponent(Graphics g) {
		if (face != null) {
			int size = face.getSize();

			double tileWidth = width/2/size;
			double tileHeight = height/2/size;

			int[] fx = new int[3];
			int[] fy = new int[3];

			fx[0] = width/6;
			fx[1] = width*5/6;
			fx[2] = mx;

			fy[0] = height*5/6;
			fy[1] = height*5/6;
			fy[2] = my;

			double v1x = (double) (fx[1]-fx[0])/(double) size;
			double v1y = (double) (fy[1]-fy[0])/(double) size;
			double v2x = (double) (fx[2]-fx[0])/(double) size;
			double v2y = (double) (fy[2]-fy[0])/(double) size;

			int maxHeight = 42;
			int waterLevel = 70;

			int k = 1;
			int counter = 1;
			boolean flip = false;
			int x=0,y=size-1;

			for (int t=0; t<size*size; t++) {

				Tile tile = face.getTile(x,y);
				tile.calculateVisualPosition();

				int[] px = new int[3];
				int[] py = new int[3];

				px[0] = fx[0] + (int) (v1x*tile.getVX() + v2x*tile.getVY());
				px[1] = (int) (px[0] + v1x);
				px[2] = (int) (px[0] + v2x);

				py[0] = fy[0] + (int) (v1y*tile.getVX() + v2y*tile.getVY());
				py[1] = (int) (py[0] + v1y);
				py[2] = (int) (py[0] + v2y);

				int h = tile.getHeight();
				int hr = (int) (h * maxHeight / 256d);

				if (tile.isFlipped()) {
					px[0] = (int) (px[2]+v1x);
					py[0] = (int) (py[2]+v1y);
				}

				for (int j = 0; j <= hr; j++) {
					for (int i = 0; i < 3; i++) {
						py[i]--;
					}
					int c = (int) ((double) h*(double) j/(double) hr);
					g.setColor(new Color(c,c,c));

					g.fillPolygon(px, py, 3);
				}
				g.setColor(Color.BLACK);
				g.drawPolygon(px, py, 3);
				if (h < waterLevel) {
					for (int j = hr; j<= waterLevel*maxHeight/256; j++) {
						for (int i = 0; i < 3; i++) {
							py[i]--;
						}
						int c = (int) (255d * (double) (j-hr) / (double) ((waterLevel*maxHeight/256d)-hr));
						g.setColor(new Color(50, 50, c));

						g.fillPolygon(px, py, 3);
					}
				}

				counter++;
				if (counter>k) {
					if (!flip) {
						flip = true;
						x = size-1;
						y = size-y;
					} else {
						flip = false;
						x = 0;
						k++;
						y = size-k;
					}
					counter = 1;
				} else {
					if (!flip) {
						x++;
					} else {
						x--;
					}
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public Face getFace() {
		return face;
	}
	public void setFace(Face face) {
		this.face = face;
	}

}
