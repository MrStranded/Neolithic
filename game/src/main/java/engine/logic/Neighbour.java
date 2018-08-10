package engine.logic;

import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;

public class Neighbour {

	private static final int EDGE_Y_IS_ZERO = 0;
	private static final int EDGE_XY_IS_SIZE = 1;
	private static final int EDGE_X_IS_ZERO = 2;

	private static final int FORMULA_A = 0;
	private static final int FORMULA_B = 1;
	private static final int FORMULA_C = 2;

	private static final int FACE_DISPLACEMENT_A = 0;
	private static final int FACE_DISPLACEMENT_B = 1;
	private static final int FACE_DISPLACEMENT_C = 2;
	private static final int FACE_DISPLACEMENT_D = 3;
	private static final int FACE_DISPLACEMENT_E = 4;
	private static final int FACE_DISPLACEMENT_F = 5;

	public static Tile[] getNeighbours(Tile tile) {
		Tile[] neighbours = new Tile[3];

		Face face = tile.getFace();
		Planet planet = face.getPlanet();
		int size = face.getSize();

		int faceX = face.getX();
		int faceY = face.getY();

		int tileX = tile.getX();
		int tileY = tile.getY();

		boolean inBiggerTriangle = tileX + tileY < size;

		int reflectedX = size - 1 - tileX;
		int reflectedY = size - 1 - tileY;

		for (int i=0; i<3; i++) {
			if (inBiggerTriangle && isOutside(size, i, reflectedX, reflectedY)) {
				int[] displacement = facePositionDisplacement(faceY, i);
				int value = tileX;
				if (i == EDGE_X_IS_ZERO) {
					value = tileY;
				}

				int[] tilePosition = edgeNeighbourCoordinates(faceY, size, i, value);

				neighbours[i] = planet.getFace(faceX + displacement[0], faceY + displacement[1]).getTile(tilePosition[0], tilePosition[1]);
			} else {
				switch (i) {
					case EDGE_Y_IS_ZERO:
						neighbours[i] = face.getTile(reflectedX, reflectedY + 1);
						break;
					case EDGE_XY_IS_SIZE:
						neighbours[i] = face.getTile(reflectedX, reflectedY);
						break;
					case EDGE_X_IS_ZERO:
						neighbours[i] = face.getTile(reflectedX + 1, reflectedY);
						break;
				}
			}
		}

		return neighbours;
	}

	private static boolean isOutside(int size, int edge, int reflectedX, int reflectedY) {
		switch (edge) {
			case EDGE_Y_IS_ZERO: return reflectedY + 1 >= size;
			case EDGE_XY_IS_SIZE: return reflectedX + reflectedY < size;
			case EDGE_X_IS_ZERO: return reflectedX + 1 >= size;
			default: return false;
		}
	}

	private static int[] edgeNeighbourCoordinates(int faceY, int size, int edge, int value) {
		int formula = 0;
		if (faceY == 0 || faceY == 3) { // top and bottom faces have formulas A, C, B
			switch (edge) {
				case EDGE_Y_IS_ZERO:
					formula = FORMULA_B;
					break;
				case EDGE_XY_IS_SIZE:
					formula = FORMULA_A;
					break;
				case EDGE_X_IS_ZERO:
					formula = FORMULA_C;
					break;
			}
		} else { // middle faces have formulas B, C, A
			switch (edge) {
				case EDGE_Y_IS_ZERO:
					formula = FORMULA_B;
					break;
				case EDGE_XY_IS_SIZE:
					formula = FORMULA_C;
					break;
				case EDGE_X_IS_ZERO:
					formula = FORMULA_A;
					break;
			}
		}

		return edgePositionFormula(size, formula, value);
	}

	private static int[] edgePositionFormula(int size, int formula, int value) {
		switch (formula) {
			case FORMULA_A:
				return new int[] {0, size - 1 - value};
			case FORMULA_B:
				return new int[] {size - 1 - value, 0};
			case FORMULA_C:
				return new int[] {size - 1 - value, value};
			default:
				return new int[] {0,0};
		}
	}

	private static int[] facePositionDisplacement(int faceY, int edge) {
		switch (faceY) {
			case 0:
				switch (edge) {
					case EDGE_Y_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_A);
					case EDGE_XY_IS_SIZE: return faceDisplacementFormula(FACE_DISPLACEMENT_C);
					case EDGE_X_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_D);
					default: return new int[] {0,0};
				}

			case 1:
				switch (edge) {
					case EDGE_Y_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_B);
					case EDGE_XY_IS_SIZE: return faceDisplacementFormula(FACE_DISPLACEMENT_A);
					case EDGE_X_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_E);
					default: return new int[] {0,0};
				}

			case 2:
				switch (edge) {
					case EDGE_Y_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_A);
					case EDGE_XY_IS_SIZE: return faceDisplacementFormula(FACE_DISPLACEMENT_B);
					case EDGE_X_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_F);
					default: return new int[] {0,0};
				}

			case 3:
				switch (edge) {
					case EDGE_Y_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_B);
					case EDGE_XY_IS_SIZE: return faceDisplacementFormula(FACE_DISPLACEMENT_D);
					case EDGE_X_IS_ZERO: return faceDisplacementFormula(FACE_DISPLACEMENT_C);
					default: return new int[] {0,0};
				}

			default:
				return new int[] {0,0};
		}
	}

	private static int[] faceDisplacementFormula(int displacement) {
		switch (displacement) {
			case FACE_DISPLACEMENT_A:
				return new int[] {0,1};
			case FACE_DISPLACEMENT_B:
				return new int[] {0,-1};
			case FACE_DISPLACEMENT_C:
				return new int[] {-1,0};
			case FACE_DISPLACEMENT_D:
				return new int[] {1,0};
			case FACE_DISPLACEMENT_E:
				return new int[] {-1,1};
			case FACE_DISPLACEMENT_F:
				return new int[] {1,-1};
			default:
				return new int[] {0,0};
		}
	}
}
