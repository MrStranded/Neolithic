package environment.world;

/**
 * Created by michael1337 on 14/06/17.
 *
 *
 */
public class Face {

	private Planet planet;
	private Tile[][] tiles;
	private int size=0;
	private Point[] corners = new Point[3];
	private int index=0;

	/**
	 * Create a Face on given Planet with given size.
	 * @param planet corresponding Planet
	 * @param size side-tile-length
	 */
	public Face (Planet planet,int size, int index) {
		this.planet = planet;
		this.index = index;
		this.size = size;

		createTiles();
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	/**
	 * Assigns the Tile array with fresh tiles.
	 */
	private void createTiles() {
		tiles = new Tile[size][size];
		for (int x=0;x<size;x++) {
			for (int y=0;y<size;y++) {
				tiles[x][y] = new Tile();
				tiles[x][y].assignFace(this, x, y);
				tiles[x][y].calculateVisualPosition();
				tiles[x][y].setHeight(0);
			}
		}
	}

	/**
	 * Defines the world Points of the Face.
	 * @param p0
	 * @param p1
	 * @param p2
	 */
	public void assignCorners(Point p0,Point p1, Point p2) {
		corners[0] = p0;
		corners[1] = p1;
		corners[2] = p2;
	}

	// ###################################################################################
	// ################################ Check functions ##################################
	// ###################################################################################

	/**
	 * Verifies whether two Tiles are neighbours.
	 * @param t original Tile
	 * @param check Tile to check against
	 * @return true, if both Tiles are on this Face and neighbours
	 */
	public boolean isNeighbour(Tile t, Tile check) {
		Tile[] neighbours = getNeighbours(t.getX(),t.getY());
		for (Tile loopT : neighbours) {
			if (loopT==check) return true;
		}
		return false;
	}

	/**
	 * Returns whether a coordinate is within the dimensions of the Tile array.
	 */
	public boolean inBounds(int x, int y) {
		if (tiles == null) return false;
		return ((x>=0)&&(y>=0)&&(x<size)&&(y<size));
	}

	/**
	 * Checks whether a Tile is on the edge of the Face.
	 * @param x coordinate of the Tile
	 * @param y coordinate of the Tile
	 * @return true if the Tile is on an edge of the Face
	 */
	public boolean isOnEdge(int x,int y) {
		if (y==0) return true;
		if (x==0) return true;
		if (x+y==size-1) return true;
		return false;
	}

	/**
	 * Returns how many corners this Face shares with another Face.
	 * @param other the other Face
	 * @return number of shared corners (0-3)
	 */
	public int numberOfSharedCorners(Face other) {
		int n=0;
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				if (corners[i]==other.getCorner(j)) n++;
			}
		}
		return n;
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

	public int getSize() { return size; }

	public Tile getTile(int x,int y) {
		if (inBounds(x,y)) return tiles[x][y];
		return null;
	}

	/**
	 * Returns all neighbouring Tiles of a given coordinate.
	 * @param tx x coordinate of the Tile
	 * @param ty y coordinate of the Tile
	 * @return the three neighbouring Tiles
	 */
	public Tile[] getNeighbours(int tx,int ty) {
		Tile[] neighbours = new Tile[3]; // there are always exactly three neighbours
		int n=0;
		// neighbours on same face
		// calculating the position is way faster than traversing the array

		int nx = size-tx-1;
		int ny = size-ty-1;

		if (tiles[tx][ty].isFlipped() != tiles[nx][ny].isFlipped()) { // the first neighbour is only correct when it has a different flip (on other side of array)
			neighbours[n] = tiles[nx][ny];
			n++;
		}
		if (inBounds(nx+1,ny)) { // second neighbour - insecure to exist
			neighbours[n] = tiles[nx+1][ny];
			n++;
		}
		if (inBounds(nx,ny+1)) { // third neighbour - insecure to exist
			neighbours[n] = tiles[nx][ny+1];
			n++;
		}

		// neighbours on other faces
		if ((n<3)&&(isOnEdge(tx,ty))) {
			Face[] neighbourFaces = planet.getNeighbours(this);
			for (Face face : neighbourFaces) {
				Point[] sharedCorners = getSharedCorners(face);
				int nE = getNAlongEdge(sharedCorners[0],sharedCorners[1],tiles[tx][ty]);
				if (nE!=-1) {
					int c1 = face.getCornerIndex(sharedCorners[0]);
					int c2 = face.getCornerIndex(sharedCorners[1]);
					if ((c1!=-1)&&(c2!=-1)) {
						Tile[] otherEdge = face.getTilesAlongEdge(c1, c2);
						if (nE>=0) {
							for (Tile oT : otherEdge) {
								if (face.getNAlongEdge(sharedCorners[0],sharedCorners[1],oT)==nE) {
									neighbours[n] = oT;
									n++;
									if (n==3) break;
								}
							}
						}
					}
					if (n==3) break;
				}
			}
		}
		return neighbours;
	}

	public Point getCorner(int i) {
		if ((i<0)||(i>2)) return null;
		return corners[i];
	}

	public int getCornerIndex(Point corner) {
		for (int i=0;i<3;i++) {
			if (corners[i]==corner) return i;
		}
		return -1;
	}

	/**
	 * Returns the coordinates in an array with size 3x3 where the first dimension is the index of the corner (0-2) and the second is the coordinates (x,y,z).
	 * @return coordinates in a double[][] array
	 */
	public double[][] getCornerCoordinates() {
		double[][] p = new double[3][3]; // first parameter: corner 0-2, second: x,y,z
		for (int i=0;i<3;i++) {
			Point corner = getCorner(i);
			p[i][0] = corner.getX();
			p[i][1] = corner.getY();
			p[i][2] = corner.getZ();
		}
		return p;
	}

	/**
	 * Returns an array with the corners (Points) that this Face shares with another.
	 * @param other Face
	 * @return Point[] array with shared corners
	 */
	private Point[] getSharedCorners(Face other) {
		Point[] points = new Point[3];
		int n=0;
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				if (corners[i]==other.getCorner(j)) {
					points[n]=corners[i];
					n++;
				}
			}
		}
		Point[] newPoints = new Point[n];
		for (int i=0;i<n;i++) {
			newPoints[i] = points[i];
		}
		return newPoints;
	}

	/**
	 * Returns the "coordinate" that a Tile has along two given Points.
	 * @param c1 "from": the first corner (Point)
	 * @param c2 "to": the second corner (Point)
	 * @param t the asked Tile
	 * @return the coordinate which lies between 0 and size-1
	 */
	public int getNAlongEdge(Point c1,Point c2,Tile t) {
		if (c1==corners[0]) {
			if (c2==corners[1]) return getNAlongXEdge(t);
			if (c2==corners[2]) return getNAlongYEdge(t);
		}
		if (c1==corners[1]) {
			if (c2==corners[0]) return size-getNAlongXEdge(t)-1;
			if (c2==corners[2]) return getNAlongXYEdge(t);
		}
		if (c1==corners[2]) {
			if (c2==corners[0]) return size-getNAlongYEdge(t)-1;
			if (c2==corners[1]) return size-getNAlongXYEdge(t)-1;
		}
		return -1;
	}

	private int getNAlongXEdge(Tile t) {
		if (t.getY()!=0) return -1;
		return t.getX();
	}
	private int getNAlongYEdge(Tile t) {
		if (t.getX()!=0) return -1;
		return t.getY();
	}
	private int getNAlongXYEdge(Tile t) {
		if (t.getX()+t.getY()!=size-1) return -1;
		return t.getY();
	}

	/**
	 * Returns all the Tiles along the edge between two corners (Points).
	 * @param c1 "from": index of the first corner (Point)
	 * @param c2 "to": index of the second corner (Point)
	 * @return Tile[] tiles along the two corners.
	 */
	public Tile[] getTilesAlongEdge(int c1,int c2) {
		if (c2<c1) {
			int tmp = c1;
			c1=c2;c2=tmp;
		}
		Tile[] edge = new Tile[size];
		if (c1==0) {
			if (c2==1) {
				for (int i=0;i<size;i++) { edge[i]=tiles[i][0]; }
			}
			if (c2==2) {
				for (int i=0;i<size;i++) { edge[i]=tiles[0][i]; }
			}
		}
		if (c1==1) {
			if (c2==2) {
				for (int i=0;i<size;i++) { edge[i]=tiles[size-i-1][i]; }
			}
		}
		return edge;
	}

	public int getIndex() {
		return index;
	}
}
