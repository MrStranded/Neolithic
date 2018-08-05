package logic;

import engine.data.Planet;
import engine.data.Tile;
import engine.logic.Neighbour;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeighbourTest {

	@Test
	public void testFaceNeighboursOnSameFace() {
		Planet planet = new Planet(8);

		Tile[] neighbours;

		Tile t1 = planet.getFace(1,2).getTile(3,2);
		neighbours = Neighbour.getNeighbours(t1);
		assertEquals(neighbours[0], planet.getFace(1,2).getTile(4+1, 5));
		assertEquals(neighbours[1], planet.getFace(1,2).getTile(4, 5+1));
		assertEquals(neighbours[2], planet.getFace(1,2).getTile(4, 5));

		Tile t2 = planet.getFace(3,0).getTile(7,7);
		neighbours = Neighbour.getNeighbours(t2);
		assertEquals(neighbours[0], planet.getFace(3,0).getTile(0+1, 0));
		assertEquals(neighbours[1], planet.getFace(3,0).getTile(0, 0+1));
		assertEquals(neighbours[2], planet.getFace(3,0).getTile(0, 0));
	}

	@Test
	public void testFaceNeighboursOnDifferentFace() {
		Planet planet = new Planet(8);

		Tile[] neighbours;

		// EDGE_X_IS_ZERO
		Tile t1 = planet.getFace(0,0).getTile(0,3);
		neighbours = Neighbour.getNeighbours(t1);

		assertEquals(neighbours[0], planet.getFace(1,0).getTile(4, 3));

		// EDGE_Y_IS_ZERO
		Tile t2 = planet.getFace(5,1).getTile(2,0);
		neighbours = Neighbour.getNeighbours(t2);

		assertEquals(0, neighbours[1].getFace().getX());
		assertEquals(0, neighbours[1].getFace().getY());
		assertEquals(5, neighbours[1].getX());
		assertEquals(0, neighbours[1].getY());

		// EDGE_XY_IS_SIZE
		Tile t3 = planet.getFace(2,3).getTile(5,2);
		neighbours = Neighbour.getNeighbours(t3);

		assertEquals(3, neighbours[2].getFace().getX());
		assertEquals(3, neighbours[2].getFace().getY());
		assertEquals(0, neighbours[2].getX());
		assertEquals(2, neighbours[2].getY());
	}

}
