package gui.graphics;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Michael on 08.11.2017.
 */
public class Utils {

	public static String loadResource(String fileName) throws Exception {
		String result;
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
			Scanner scanner = new Scanner(in, "UTF-8")) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}

}
