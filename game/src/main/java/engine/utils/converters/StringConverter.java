package engine.utils.converters;

public class StringConverter {

	/**
	 * Transforms a string into a (hopefully sufficiently) unique number.
	 * @param text from which to create id
	 * @return id, calculated based on given text
	 */
	public static int toID(String text) {
		int i = 1;
		int id = 0;
		for (char c : text.toCharArray()) {
			id += c * i;
			i *= 256; // nr of ascii chars
		}
		return id;
	}

}
