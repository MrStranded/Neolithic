package gui;

import environment.world.Entity;
import environment.world.Face;
import environment.world.Tile;

import java.awt.*;
import java.awt.event.MouseEvent;

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

	/**
	 * The big master method for drawing Faces. Too big as it is now.
	 */
	public void paintComponent(Graphics g) {

		if (face != null) {

			// %%%%%%%%%%%%%%%%%%%%%%%% initializing necessary variables concerning orientation and so forth

			int size = face.getSize();

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

			double[] px = new double[3];
			double[] py = new double[3];

			// %%%%%%%%%%%%%%%%%%%%%%%% these variables go through the tiles in a special manner to go sure that tiles further away are drawn first

			int k = 1;
			int counter = 1;
			boolean flip = false;
			int x=0,y=size-1;

			// %%%%%%%%%%%%%%%%%%%%%%%% going through all tiles

			for (int t=0; t<size*size; t++) {

				Tile tile = face.getTile(x,y);

				// %%%%%%%%%%%%%%%%%%%%%%%% calculating the position of the tile

				px[0] = fx[0] + (v1x*tile.getVX() + v2x*tile.getVY());
				px[1] = (px[0] + v1x);
				px[2] = (px[0] + v2x);

				py[0] = fy[0] + (v1y*tile.getVX() + v2y*tile.getVY());
				py[1] = (py[0] + v1y);
				py[2] = (py[0] + v2y);

				int h = tile.getHeight();

				// %%%%%%%%%%%%%%%%%%%%%%%% flipping it if necessary

				if (tile.isFlipped()) {
					px[0] = (px[2]+v1x);
					py[0] = (py[2]+v1y);
				}

				// %%%%%%%%%%%%%%%%%%%%%%%% converting into those sweet integers

				int[] ix = new int[3];
				int[] iy = new int[3];
				for (int i = 0; i < 3; i++) {
					ix[i] = (int) px[i];
					iy[i] = (int) py[i];
				}

				// %%%%%%%%%%%%%%%%%%%%%%%% actually drawing the tile

				for (int j = 0; j <= h; j++) {

					// %%%%%%%%%%%%%%%%%%%%%%%% initializing the color of the tile

					int red = tile.getRed();
					red = red / 2 + (int) ((double) (red / 2) * (double) h / 255d);
					int green = tile.getGreen();
					green = green / 2 + (int) ((double) (green / 2) * (double) h / 255d);
					int blue = tile.getBlue();
					blue = blue / 2 + (int) ((double) (blue / 2) * (double) h / 255d);
					g.setColor(new Color(red, green, blue));

					// %%%%%%%%%%%%%%%%%%%%%%%% drawing the tile

					g.drawPolygon(ix, iy, 3);
					if ((j==h)) {
						g.fillPolygon(ix, iy, 3);
					}

					// %%%%%%%%%%%%%%%%%%%%%%%% setting up the position for the next layer

					for (int i = 0; i < 3; i++) {
						iy[i]--;
					}
				}

				// %%%%%%%%%%%%%%%%%%%%%%%% drawing the tiles entities if there are some

				if ((tile.getEntities()!=null)&&(!tile.getEntities().isEmpty())) {
					int ex = (ix[0]+ix[1]+ix[2])/3;
					int ey = (iy[0]+iy[1]+iy[2])/3;

					g.setColor(Color.GREEN);
					for (Entity entity : tile.getEntities()) {
						g.drawString(String.valueOf(entity.getThumbnail()),ex-g.getFontMetrics().charWidth(entity.getThumbnail())/2,ey);
					}
				}

				// %%%%%%%%%%%%%%%%%%%%%%%% temporary cloud drawing

				/*if (tile.getHumidity()>=100) {
					g.setColor(Color.WHITE);

					for (int i = 0; i < 3; i++) {
						iy[i]-=(255-h);
					}

					g.drawPolygon(ix, iy, 3);
				}*/

				// %%%%%%%%%%%%%%%%%%%%%%%% temporary rain drawing

				/*g.setColor(Color.BLUE);
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
				}*/

				// %%%%%%%%%%%%%%%%%%%%%%%% temporary water / sea drawing

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

				// %%%%%%%%%%%%%%%%%%%%%%%% a really messy way to go through all the tiles in the visibly correct order

				counter++; // the counter does not directly correspond to going to the next tile
				if (counter>k) { // when crossing the diagonal of the tile array
					if (!flip) { // on the small side of the array
						flip = true;
						x = size-1;
						y = size-y;
					} else { // on the big side of the array
						flip = false;
						x = 0;
						k++;
						y = size-k;
					}
					counter = 1; // starting at the start of a line
				} else {
					if (!flip) {
						x++; // going to the next tile on the big side of the array
					} else {
						x--; // going to the next tile on the small side of the array
					}
				}
			}
		}
	}

	/**
	 * Keeps track where the mouse was last clicked.
	 */
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
