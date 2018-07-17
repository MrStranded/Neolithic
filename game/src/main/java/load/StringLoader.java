package load;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StringLoader {

	/**
	 * Reads all lines from a file, concatenates them with line breaks inbetween and returns them as a single String.
	 * @param fileName relative path from the root folder
	 * @return String with contents of file
	 * @throws Exception if the file is not found
	 */
	public static String read(String fileName) throws Exception {

		BufferedReader bufferedReader;
		String out = null;
		bufferedReader = new BufferedReader(new FileReader(fileName));

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (out == null) {
				out = line;
			} else {
				out = out + "\n" + line;
			}
		}

		return out;
	}

	/**
	 * Reads all lines from a file, stores them in a List and returns the List.
	 * @param fileName relative path from the root folder
	 * @return List with all lines from file in order
	 * @throws Exception if the file is not found
	 */
	public static List<String> readAllLines(String fileName) throws Exception {

		BufferedReader bufferedReader;
		List<String> out = new ArrayList<>();
		bufferedReader = new BufferedReader(new FileReader(fileName));

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			out.add(line);
		}

		return out;
	}
}
