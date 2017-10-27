package gui;

import data.Data;
import data.proto.Value;
import engine.EntityValueProcessor;
import environment.world.Entity;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Tile;

import java.awt.*;

/**
 * Created by Michael on 11.07.2017.
 *
 * A class to draw map faces.
 */
public class DrawPlanet extends Draw {

	private Planet planet;

	// ###################################################################################
	// ################################ Drawing ##########################################
	// ###################################################################################

	double anglex = Math.PI/2+0.5d;
	double angley = Math.PI/2+0.5d;

	double midx = 0;
	double midy = 0;

	/**
	 * The big master method for drawing Faces. Too big as it is now.
	 */
	public void paintComponent(Graphics g) {

//		if (!Parser.isFinished()) {
//			System.out.println();
//			System.out.println("DrawPlanet.paintComponent.showParserProgress: "+Parser.getProgress());
//		}

		midx = width/2;
		midy = height/2;

		if (planet != null) {
			// planet rotation
			//anglex += 0.005d;
			//angley += 0.0025d;

			// face corner rotation
			for (Face face : planet.getFaces()) {
				if (face != null) {
					double[][] coordinates = rotateCoordiantes(face);

					double[] fx = new double[3];
					double[] fy = new double[3];

					double y = 0;

					for (int i=0; i<3; i++) {
						fx[i] = midx + coordinates[i][0]*planet.getRadius();
						fy[i] = midy + coordinates[i][1]*planet.getRadius();
						y += coordinates[i][2];
					}

					if (y>=0) drawFace(g,face,fx,fy);
				}
			}
		}
	}

	/**
	 * Basically a copy of the content of the DrawFace class.
	 * @param g the Graphics object
	 * @param face the Face to be drawn
	 * @param fx x coordinates of face
	 * @param fy y coordinates of face
	 */
	private void drawFace(Graphics g, Face face, double[] fx, double[] fy) {

		// %%%%%%%%%%%%%%%%%%%%%%%% initializing necessary variables concerning orientation and so forth

		int size = face.getSize();

		double v1x = (fx[1]-fx[0])/(double) size;
		double v1y = (fy[1]-fy[0])/(double) size;
		double v2x = (fx[2]-fx[0])/(double) size;
		double v2y = (fy[2]-fy[0])/(double) size;

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

			// %%%%%%%%%%%%%%%%%%%%%%%% scaling it to fit the height

			double mh = 1000d;
			double f = (h+mh)/(255d+mh);
			for (int i = 0; i < 3; i++) {
				px[i] = midx + (px[i]-midx)*f;
				py[i] = midy + (py[i]-midy)*f;
			}

			// %%%%%%%%%%%%%%%%%%%%%%%% converting into those sweet integers

			int[] ix = new int[3];
			int[] iy = new int[3];
			for (int i = 0; i < 3; i++) {
				ix[i] = (int) px[i];
				iy[i] = (int) py[i];
			}

			// %%%%%%%%%%%%%%%%%%%%%%%% actually drawing the tile

//			int red = 220;//tile.getRed();
//			int green = 200;//tile.getGreen();
//			int blue = 40;//tile.getBlue();
//
//			if (water) {
//				red = 70;
//				green = 180;
//				blue = 240;
//			}
//
//			red = red / 2 + (int) ((double) (red / 2) * (double) h / 255d);
//			green = green / 2 + (int) ((double) (green / 2) * (double) h / 255d);
//			blue = blue / 2 + (int) ((double) (blue / 2) * (double) h / 255d);

//			int red = 128, green = 128, blue = 128;
//
//			if (tile.getSelf() != null) {
//				Variable color = tile.getSelf().tryToGetVariable("color");
//				if (color != null) {
//					red = (int) (color.getValue(0).getNumber());
//					green = (int) (color.getValue(1).getNumber());
//					blue = (int) (color.getValue(2).getNumber());
//				}
//			}

			g.setColor(adjustColorToHeight(EntityValueProcessor.getEntityColor(tile.getSelf()),h));

			// %%%%%%%%%%%%%%%%%%%%%%%% drawing the tile

			g.fillPolygon(ix, iy, 3);

			// %%%%%%%%%%%%%%%%%%%%%%%% drawing the tiles entities if there are some

			if ((tile.getEntities()!=null)&&(!tile.getEntities().isEmpty())) {
				int ex = (ix[0]+ix[1]+ix[2])/3;
				int ey = (iy[0]+iy[1]+iy[2])/3+g.getFontMetrics().getHeight()/3;

				for (Entity entity : tile.getEntities()) {
					g.setColor(EntityValueProcessor.getEntityColor(entity));

					g.drawString(String.valueOf(entity.getThumbnail()),ex-g.getFontMetrics().charWidth(entity.getThumbnail())/2,ey);
				}
			}

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

	/**
	 * Returns rotated coordinates, depending on the two variables anglex and angley.
	 */
	private double[][] rotateCoordiantes(Face face) {
		double[][] v = face.getCornerCoordinates();
		double[][] nv = new double[3][3];

		for (int i=0; i<3; i++) {
			nv[i][0] = Math.cos(anglex)*v[i][0]
					+ Math.sin(anglex)*Math.cos(angley)*v[i][1]
					+ Math.sin(anglex)*Math.sin(angley)*v[i][2];
			nv[i][1] = -Math.sin(anglex)*v[i][0]
					+ Math.cos(anglex)*Math.cos(angley)*v[i][1]
					+ Math.cos(anglex)*Math.sin(angley)*v[i][2];
			nv[i][2] = -Math.sin(angley)*v[i][1]
					+ Math.cos(angley)*v[i][2];
		}

		return nv;
	}

	private Color adjustColorToHeight(Color c, int height) {
		double f = (double) height / 255d;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		r = r / 2 + (int) ((double) (r / 2) * f);
		g = g / 2 + (int) ((double) (g / 2) * f);
		b = b / 2 + (int) ((double) (b / 2) * f);
		return new Color(r,g,b);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public Planet getPlanet() {
		return planet;
	}
	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
}
