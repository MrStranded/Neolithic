package environment.meteorology;

/**
 * Created by Michael on 14.07.2017.
 *
 * This class represents rain drops.
 */
public class RainDrop {

	private double x,y;
	private int height;

	public RainDrop() {
		height = 255;
		x = Math.random();
		y = Math.random();
		if (x+y>1d) {
			x = 1-x;
			y = 1-y;
		}
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
