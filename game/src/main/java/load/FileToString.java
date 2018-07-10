package load;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileToString {

	public static String read(String fileName) {

		BufferedReader bufferedReader;
		String out = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (out == null) {
					out = line;
				} else {
					out = out + "\n" + line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

}
