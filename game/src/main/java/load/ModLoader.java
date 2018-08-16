package load;

import constants.ResourcePathConstants;
import engine.parser.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModLoader {

	public static List<String> loadMods() {
		List<String> mods = new ArrayList<>(4);

		File modFolder = new File(ResourcePathConstants.MOD_FOLDER);

		if (modFolder.exists()) {
			File modFile = new File(ResourcePathConstants.MOD_LOAD_ORDER_FILE);

			if (modFile.exists() && modFile.isFile()) {
				try {
					FileReader fileReader = new FileReader(modFile);
					BufferedReader bufferedReader = new BufferedReader(fileReader);

					// actually reading in the mods
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						mods.add(line);
					}
				} catch (FileNotFoundException e) {
					Logger.error("Could not open 'loadorder.txt' file in 'data' directory!");
				} catch (IOException e) {
					Logger.error("Could not read line from 'loadorder.txt' file in 'data' directory!");
				}
			} else {
				Logger.error("No 'loadorder.txt' file in 'data' directory!");
			}
		} else {
			Logger.error("No 'data' folder in game directory!");
		}

		return mods;
	}
}
