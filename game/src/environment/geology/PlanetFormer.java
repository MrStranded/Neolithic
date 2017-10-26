package environment.geology;

import data.Data;
import data.personal.Attribute;
import data.proto.ProtoAttribute;
import environment.world.Entity;
import environment.world.Face;
import environment.world.Planet;
import environment.world.Tile;
import threads.DependantThread;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class forms hills and mountains on a mountain.
 */
public class PlanetFormer extends DependantThread {

	private static Planet planet;

	public static void setPlanet (Planet p) {
		planet = p;
	}

	public void run () {
		waitForDependantThread();

		System.out.println("Starting topology generation.");
		generateTopology();
		System.out.println("Planting trees.");
		generateTrees();
		System.out.println("Growing life forms.");
		generateCreatures();
		System.out.println("Planet formation completed.");
	}

	/**
	 * Generates a handful of creatures on face(0).
	 */
	public static void generateCreatures() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			Face face = planet.getFace(0);
			if (face != null) {

				ProtoAttribute speed = new ProtoAttribute("Speed","attSpeed",false,true);
				Data.addProtoAttribute(speed);
				int speedId = Data.getProtoAttributeId(speed.getTextId());

				Tile stile = face.getTile((int) (Math.random()*face.getSize()),(int) (Math.random()*face.getSize()));
				Entity semira = new Entity(stile,'S');
				stile.addEntity(semira);
				planet.signEntity(semira);

				Attribute scSpeed = new Attribute(speedId,(int) (Math.random()*3)+1);
				semira.addAttribute(scSpeed);

				for (int i=0; i<7; i++) {
					Tile tile = face.getTile((int) (Math.random()*face.getSize()),(int) (Math.random()*face.getSize()));
					Entity creature = new Entity(tile,'C');
					tile.addEntity(creature);
					planet.signEntity(creature);

					Attribute cSpeed = new Attribute(speedId,(int) (Math.random()*3)+1);
					creature.addAttribute(cSpeed);
				}
			}
		}
	}

	/**
	 * Generates trees on the whole planet.
	 */
	public static void generateTrees() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) {
					for (int x=0; x<face.getSize(); x++) {
						for (int y=0; y<face.getSize(); y++) {
							if ((face.getTile(x,y).getHeight()>100) && (Math.random()>0.95d)) {
								face.getTile(x,y).addEntity(new Entity(face.getTile(x,y),'T'));
//								try {
//									sleep(5);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Generates a Topology for the whole planet.
	 */
	public static void generateTopology() {
		if ((planet!=null)&&(planet.getFaces()!=null)) {
			for (Face face : planet.getFaces()) {
				if (face != null) generateFaceTopology(face);
			}
		}
	}

	private static void generateFaceTopology(Face face) {
		int size = face.getSize();
		int hills = 2 + (int) (Math.random()*5);
		int hillSize = 8 + (int) (Math.random()*8);
		int stepSize = 1 + (int) (Math.random()*2);
		int elevation = 156 + (int) (Math.random()*100);

		int x=0,y=0;

		for (int j=0; j<hills; j++) {
			x = (int) (size * Math.random());
			y = (int) (size * Math.random());
			for (int i = 0; i < hillSize; i++) {
				x = (x + (int) (stepSize * Math.random())) % size;
				y = (y + (int) (stepSize * Math.random())) % size;
				elevateTile(face, face.getTile(x, y), (int) (elevation*Math.random()));

//				try {
//					sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		}
	}

	private static void raiseTile(Face face, Tile tile, int level) {
		int h = tile.getHeight()+level;
		if (h>255) h = 255;
		tile.setHeight(h);

		level *= (Math.random()/4d+0.375d);
		if (level>0) {
			Tile[] neighbours = face.getNeighbours(tile.getX(),tile.getY());
			for (Tile t : neighbours) {
				if (t!=null) raiseTile(t.getFace(),t,level);
			}
		}
	}

	private static void elevateTile(Face face, Tile tile, int level) {
		if (level>255) level = 255;
		if (tile.getHeight() < level) {
			tile.setHeight(level);

			level -= ((Math.random()+0.5d)*8);
			if (level > 0) {
				Tile[] neighbours = face.getNeighbours(tile.getX(), tile.getY());
				for (Tile t : neighbours) {
					if (t != null) elevateTile(t.getFace(), t, level);
				}
			}
		}
	}

}
