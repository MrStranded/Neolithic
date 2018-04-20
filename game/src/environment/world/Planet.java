package environment.world;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Michael on 14.06.2017.
 *
 *
 */
public class Planet {

	private Face[] faces = new Face[20];
	private Point[] worldPoints = new Point[12];
	private int size; // side length of faces
	private double radius; // radius of planet
	private double tileHeightCorrection = 32d; // is used in CoordinateCalculator.getHeightFactor to normalize tile heights

	private ConcurrentLinkedDeque<Entity> entityProcessingQueue = new ConcurrentLinkedDeque<>();

	/**
	 * Create a Planet with given specifications.
	 * @param size side-tile-length
	 * @param radius from center to closest edge
	 */
	public Planet (int size, double radius) {
		this.size = size;
		this.radius = radius;

		createWorldPoints();
		createFaces(size);
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	/**
	 * Assigns the 20 Faces.
	 * @param size side-tile-length
	 */
	private void createFaces(int size) {
		// regular faces
		for (int i=0;i<3;i++) {
			for (int j=0;j<2;j++) {
				for (int k=0;k<2;k++) {
					int n = i*4 + j*2 + k;
					int c1 = i*4 + j;
					int c2 = c1 + 2;
					int c3 = (n + 4)%12;

					assignFace(n,c1,c2,c3);
				}
			}
		}

		// odd faces
		for (int i=0;i<2;i++) {
			for (int j=0;j<2;j++) {
				for (int k=0;k<2;k++) {
					int n = 12 + i + j*2 + k*4;
					int c1 = 0 + i + j*2;
					int c2 = 4 + k + i*2;
					int c3 = 8 + j + k*2;

					assignFace(n,c1,c2,c3);
				}
			}
		}
	}

	/**
	 * Assigns the appropriate slot in the faces array with the corresponding worldPoints.
	 * @param n index of face
	 * @param c1 first worldPoint index
	 * @param c2 second worldPoint index
	 * @param c3 third worldPoint index
	 */
	private void assignFace(int n, int c1, int c2, int c3) {
		faces[n] = new Face(this,size,n);

		if (isRightOrientated(n)) {
			int tmp = c2;
			c2 = c3;
			c3 = tmp;
		}
		faces[n].assignCorners(worldPoints[c1], worldPoints[c2], worldPoints[c3]);
	}

	/**
	 * calculates, whether the face is right-oriented, based on its index
	 * @param n index
	 * @return true, when right-oriented
	 */
	private boolean isRightOrientated(int n) {
		if (n<16) return (((n+3)/2)%2 == 0);
		return (((n+1)/2)%2 == 0);
	}

	/**
	 * Creates the 12 Points of the isocaeder with the golden ratio.
	 */
	private void createWorldPoints() {
		double phi = 1.61d; // golden Ratio
		double c[] = new double[3]; // coordiantes

		for (int i=0;i<3;i++) {
			for (int j=-1;j<=1;j+=2) {
				for (int k=-1;k<=1;k+=2) {
					c[(i+0)%3] = j*radius;
					c[(i+1)%3] = k*phi*radius;
					c[(i+2)%3] = 0;

					int n=i*4 + (j+1) + (k+1)/2;
					worldPoints[n] = new Point(c[0],c[1],c[2],(byte) i);
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void signEntity(Entity entity) {
		entityProcessingQueue.add(entity);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public ConcurrentLinkedDeque<Entity> getEntityProcessingQueue() {
		return entityProcessingQueue;
	}

	public Face getFace(int i) {
		if ((faces!=null)&&(i<faces.length)&&(i>=0)) return faces[i];
		return null;
	}

	public Face[] getFaces() { return faces; }

	public double getRadius() { return radius; }

	public int getSize() {
		return size;
	}

	/**
	 * Gives the three Faces that lie next to the given Face.
	 * @param face for which the neighbours are searched
	 * @return three neighbour Faces
	 */
	public Face[] getNeighbours(Face face) {
		Face[] neighbours = new Face[3];
		int n=0;
		for (Face f : faces) {
			if (f!=face) {
				if (face.numberOfSharedCorners(f)==2) {
					neighbours[n]=f;
					n++;
				}
			}
		}
		return neighbours;
	}

	/**
	 * Is used in CoordinateCalculator.getHeightFactor to normalize tile heights.
	 * @return
	 */
	public double getTileHeightCorrection() {
		return tileHeightCorrection;
	}
}
