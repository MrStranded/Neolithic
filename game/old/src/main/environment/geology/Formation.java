package main.environment.geology;

import main.data.proto.Container;

/**
 * Created by michael1337 on 01/11/17.
 */
public class Formation {

	private double spawnPercent = 0;
	private double continuationPercent = 0;
	private int minHeight = 0;
	private int maxHeight = 0;
	private int deltaHeight = 8;
	private int deltaDerivation = 0;
	
	public Formation (Container container) {
			if (container.tryToGet("spawnPercent") != null) {
				spawnPercent = Double.parseDouble(container.getString("spawnPercent", 0));
				continuationPercent = Double.parseDouble(container.getString("spawnPercent", 1));
			}
			minHeight = container.getInt("heightParameters",0);
			maxHeight = container.getInt("heightParameters",1);
			deltaHeight = container.getInt("heightParameters",2);
			deltaDerivation = container.getInt("heightParameters",3);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public double getSpawnPercent() {
		return spawnPercent;
	}

	public double getContinuationPercent() {
		return continuationPercent;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getDeltaHeight() {
		return deltaHeight;
	}

	public int getDeltaDerivation() {
		return deltaDerivation;
	}
}
