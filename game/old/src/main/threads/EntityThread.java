package main.threads;

import main.data.Data;
import main.environment.world.Entity;
import main.environment.world.Planet;
import main.environment.world.Tile;

import java.util.Iterator;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class handles the actions of the planet's entities one-by-one.
 */
public class EntityThread extends Thread {

	private Iterator<Entity> entityIterator;
	private Planet planet;
	private int sleepTime;

	public EntityThread(Planet planet, int sleepTime) {
		this.planet = planet;
		this.sleepTime = sleepTime;
		if (planet != null) {
			entityIterator = planet.getEntityProcessingQueue().iterator();
		}
	}

	public void run() {
		while (true) {
			if (!entityIterator.hasNext()) {
				entityIterator = planet.getEntityProcessingQueue().iterator();
			}
			if (entityIterator.hasNext()) {
				Entity entity = entityIterator.next();
				if (entity != null) {
					int speed = entity.getAttribute(Data.getProtoAttributeId("attSpeed"));
					while (speed > 0) {
						Tile tile = entity.getTile();
						Tile[] neighbours = tile.getFace().getNeighbours(tile.getX(), tile.getY());
						Tile target = neighbours[(int) (Math.random() * neighbours.length)];

						boolean move = true;
						if (entity.getThumbnail() == 'C') {
							move = (target.getEntities().size() == 0) && ((target.getHeight() >= 100) || (target.getHeight() >= tile.getHeight()));
						} else if (entity.getThumbnail() == 'S') {
							move = ((target.getHeight() <= 100) || (target.getHeight() <= tile.getHeight()));
						}

						if (move) {
							tile.removeEntity(entity);
							target.addEntity(entity);
							entity.setTile(target);

							try {
								sleep(sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
						speed--;
					}
				}
			}
		}
	}

}
