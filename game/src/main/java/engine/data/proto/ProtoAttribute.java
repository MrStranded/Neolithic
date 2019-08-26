package engine.data.proto;

import java.awt.*;

public class ProtoAttribute {

	private String textID;
	private String name = "Unnamed attribute";

	private boolean inherited = false;
	private double mutationChance = 0;
	private double mutationExtent = 0;

	private boolean hasLowerBound = false;
	private int lowerBound = 0;
	private boolean hasUpperBound = false;
	private int upperBound = 0;

	private Color guiColor = new Color(0,0,0);

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public ProtoAttribute(String textID) {
		this.textID = textID;
	}

	public String getTextID() {
		return textID;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public boolean isInherited() {
		return inherited;
	}
	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

	public double getMutationChance() {
		return mutationChance;
	}
	public void setMutationChance(double mutationChance) {
		this.mutationChance = mutationChance;
	}

	public double getMutationExtent() {
		return mutationExtent;
	}
	public void setMutationExtent(double mutationExtent) {
		this.mutationExtent = mutationExtent;
	}

	public boolean isHasLowerBound() {
		return hasLowerBound;
	}
	public void setHasLowerBound(boolean hasLowerBound) {
		this.hasLowerBound = hasLowerBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		hasLowerBound = true;
		this.lowerBound = lowerBound;
	}

	public boolean isHasUpperBound() {
		return hasUpperBound;
	}
	public void setHasUpperBound(boolean hasUpperBound) {
		this.hasUpperBound = hasUpperBound;
	}

	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		hasUpperBound = true;
		this.upperBound = upperBound;
	}

	public Color getGuiColor() {
		return guiColor;
	}
	public void setGuiColor(Color guiColor) {
		this.guiColor = guiColor;
	}
}
