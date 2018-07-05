package renderer.image;

public class Image<T> {

	private int width,height;
	private T[] data;

	@SuppressWarnings("unchecked") // we have to do that because the compiler does not like how we create our data array
	public Image(int width, int height) {
		this.width = width;
		this.height = height;

		data = (T[]) new Object[width*height];
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public T get(int x, int y) {
		return data[x + y*width];
	}
	public void set(int x, int y, T element) {
		data[x + y*width] = element;
	}

}
