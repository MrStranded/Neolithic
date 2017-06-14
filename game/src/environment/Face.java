package environment;

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

	// ################################ Constructor
	public Face (Planet planet,int size) {

		this.planet = planet;
		createTiles(size);

	}

	// ################################ Set Up
	private void createTiles(int size) {
		this.size = size;
		tiles = new Tile[size][size];
		for (int x=0;x<size;x++) {
			for (int y=0;y<size;y++) {
				tiles[x][y] = new Tile();
				tiles[x][y].assignFace(this, x, y);
				tiles[x][y].calculateVisualPosition();
			}
		}
	}

	public void assignCorners(Point p0,Point p1, Point p2) {
		corners[0] = p0;
		corners[1] = p1;
		corners[2] = p2;
	}

	// ################################ Getters
	public int getSize() { return size; }

	public Tile getTile(int x,int y) {
		if (tiles!=null) {
			if ((x>=0)&&(y>=0)&&(x<size)&&(y<size)) return tiles[x][y];
		}
		return null;
	}

	public boolean isNeighbour(Tile t, Tile check) {
		Tile[] neighbours = getNeighbours(t.getX(),t.getY());
		for (Tile loopT : neighbours) {
			if (loopT==check) return true;
		}
		return false;
	}

	public Tile[] getNeighbours(int tx,int ty) {
		Tile[] neighbours = new Tile[3]; // maximally three neighbours
		int n=0;
		// neighbours on same face
		for (int x=0;x<size;x++) {
			for (int y=0;y<size;y++) {
				int dx = tiles[x][y].getVX() - tiles[tx][ty].getVX();
				int dy = tiles[x][y].getVY() - tiles[tx][ty].getVY();
				if (dy==0) {
					if (Math.abs(dx)==1) {
						neighbours[n] = tiles[x][y];
						n++;
						if (n==3) break;
					}
				} else {
					int f = tiles[tx][ty].getFlip()? -1 : 1;
					if ((dy==-2*f)&&(dx==f)) {
						neighbours[n] = tiles[x][y];
						n++;
						if (n==3) break;
					}
				}
			}
		}
		if (n!=3) System.out.println("n = "+n);
		// neighbours on other faces
		if ((n<3)&&(isOnEdge(tx,ty))) {
			System.out.println("Check EdgeTile "+tx+","+ty);
			Face[] neighbourFaces = planet.getNeighbours(this);
			for (Face face : neighbourFaces) {
				Point[] sharedCorners = getSharedCorners(face);
				int nE = getNAlongEdge(sharedCorners[0],sharedCorners[1],tiles[tx][ty]);
				if (nE!=-1) {
					System.out.println("found right neigbour face");
					int c1 = face.getCornerIndex(sharedCorners[0]);
					int c2 = face.getCornerIndex(sharedCorners[1]);
					if ((c1!=-1)&&(c2!=-1)) {
						Tile[] otherEdge = face.getTilesAlongEdge(c1, c2);
						if (nE>=0) {
							for (Tile oT : otherEdge) {
								if (face.getNAlongEdge(sharedCorners[0],sharedCorners[1],oT)==nE) {
									System.out.println("Is Neighbour Tile! "+tx+","+ty+" and "+oT.getX()+","+oT.getY());
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

	public boolean isOnEdge(int x,int y) {
		if (y==0) return true;
		if (x==0) return true;
		if (x+y==size-1) return true;
		return false;
	}

	/*public boolean isNeighbour(Tile t,Tile n) {
		if ((Math.abs(t.getVX()-n.getVX())>1)||(Math.abs(t.getVY()-n.getVY())>2)) return false;
		if ((t.getVY()==n.getVY())&&(Math.abs(t.getVX()-n.getVX())==1)) return true;
		if (t.getVY()-2*t.getFlip()==n.getVY()) {
			if (t.getVX()+t.getFlip()==n.getVX()) return true;
		}
		return false;
	}*/

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

	public int sharesCorners(Face other) {
		int n=0;
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				if (corners[i]==other.getCorner(j)) n++;
			}
		}
		return n;
	}

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

}
