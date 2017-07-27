package environment

import environment.world.Tile
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * Created by Michael on 15.06.2017.
 */
class TileTest {

    @Test
    void setUp() {

        Tile tile = new Tile();
        assertEquals(null,tile.getFace());

    }

    /*@Test
    void testAssignFace() {

        Tile tile = new Tile();
        Face face = new Face(null,4);
        Tile.assignFace(face,1,2);

        assertEquals(face,tile.getFace());
        assertEquals(tile.getX(),1);
        assertEquals(tile.getY(),2);
        assertEquals(tile.isFlipped(),false);

    }

    @Test
    void testCalculateVisualPosition() {

        Tile tile = new Tile();
        Face face = new Face(null,4);
        Tile.assignFace(face,2,3);

        assertEquals(face,tile.getFace());
        assertEquals(2,tile.getX());
        assertEquals(3,tile.getY());
        assertEquals(true,tile.isFlipped());
        assertEquals(1,tile.getVX());
        assertEquals(0,tile.getVY());

    }*/
}
