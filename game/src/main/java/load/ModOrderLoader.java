package load;

import constants.ResourcePathConstants;
import engine.parser.utils.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModOrderLoader {

	private static List<String> mods = new ArrayList<>(4);

	public static List<String> loadMods() {
		if (! mods.isEmpty()) {
			return mods;
		}

		File modFolder = new File(ResourcePathConstants.DATA_FOLDER);
		if (! modFolder.exists()) {
			Logger.error("No 'data' folder in game directory!");
			return mods;
		}

		File modFile = new File(ResourcePathConstants.MOD_LOAD_ORDER_FILE);
		if (! modFile.exists() || ! modFile.isFile()) {
			Logger.error("No 'loadorder.txt' file in 'data' directory!");
		}

		try (FileReader fileReader = new FileReader(modFile);
			 BufferedReader bufferedReader = new BufferedReader(fileReader)) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				mods.add(line);
			}
		} catch (FileNotFoundException e) {
			Logger.error("Could not open 'loadorder.txt' file in 'data' directory!");
		} catch (IOException e) {
			Logger.error("Could not read line from 'loadorder.txt' file in 'data' directory!");
		}

		return mods;
	}

	public static void clear() {
		mods.clear();
	}

}
