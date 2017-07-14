package gui;

import environment.Face;
import environment.Shelf;
import environment.Tile;
import environment.meteorology.RainDrop;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Created by Michael on 11.07.2017.
 *
 * A class to draw map faces.
 */
public class DrawFace extends Draw {

	private Face face;
	private int mx=550,my=160;

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

			int maxHeight = 255;
			int waterLevel = 70;

			int k = 1;
			int counter = 1;
			boolean flip = false;
			int x=0,y=size-1;

			for (int t=0; t<size*size; t++) {

				Tile tile = face.getTile(x,y);

				double[] px = new double[3];
				double[] py = new double[3];

				px[0] = fx[0] + (v1x*tile.getVX() + v2x*tile.getVY());
				px[1] = (px[0] + v1x);
				px[2] = (px[0] + v2x);

				py[0] = fy[0] + (v1y*tile.getVX() + v2y*tile.getVY());
				py[1] = (py[0] + v1y);
				py[2] = (py[0] + v2y);

				int h = tile.getHeight();

				if (tile.isFlipped()) {
					px[0] = (px[2]+v1x);
					py[0] = (py[2]+v1y);
				}

				int[] ix = new int[3];
				int[] iy = new int[3];
				for (int i = 0; i < 3; i++) {
					ix[i] = (int) px[i];
					iy[i] = (int) py[i];
				}

				Shelf[] layers = tile.getLayers();

				for (int j = 0; j <= h; j++) {
					if (layers[j] != null) {
						int red = layers[j].getRed();
						red = red / 2 + (int) ((double) (red / 2) * (double) j / 255d);
						int green = layers[j].getGreen();
						green = green / 2 + (int) ((double) (green / 2) * (double) j / 255d);
						int blue = layers[j].getBlue();
						blue = blue / 2 + (int) ((double) (blue / 2) * (double) j / 255d);
						g.setColor(new Color(red, green, blue));
						if ((j==255) || (layers[j+1]==null)) {
							g.fillPolygon(ix, iy, 3);
						} else {
							g.drawPolygon(ix, iy, 3);
						}
					}
					for (int i = 0; i < 3; i++) {
						iy[i]--;
					}
				}

				if (tile.getHumidity()>=100) {
					g.setColor(Color.WHITE);

					for (int i = 0; i < 3; i++) {
						iy[i]-=(255-h);
					}

					g.drawPolygon(ix, iy, 3);
				}

				g.setColor(Color.BLUE);
				if (tile.getRain()!=null) {
					Iterator<RainDrop> rainDropIterator = tile.getRain().iterator();
					while (rainDropIterator.hasNext()) {
						try {
							RainDrop rainDrop = rainDropIterator.next();
							int rx = (int) (px[0] + v1x * rainDrop.getX() + v2x * rainDrop.getX());
							int ry = (int) (py[0] + v1y * rainDrop.getY() + v2y * rainDrop.getY()) - rainDrop.getHeight();
							g.drawLine(rx, ry, rx, ry - 3);
						} catch (ConcurrentModificationException e) {
							// it's okay. sometimes the raindrop you work on gets deleted
						}
					}
				}
				//g.drawPolygon(ix, iy, 3);
				/*if (h < waterLevel) {
					for (int j = hr; j<= waterLevel*maxHeight/255; j++) {
						for (int i = 0; i < 3; i++) {
							py[i]--;
						}
						int c = (int) (255d * (double) (j-hr) / (double) ((waterLevel*maxHeight/255d)-hr));
						g.setColor(new Color(50, 50, c));

						g.fillPolygon(px, py, 3);
					}
				}*/

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
