package parser;

import data.Data;
import enums.script.Sign;
import environment.world.Entity;
import log.Log;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;

/**
 * Created by Michael on 08.08.2017.
 *
 * The parser class. This class reads out all the files and interprets the raw data.
 */
public class Parser {

	/**
	 * Internal variables to keep track of the loading status.
	 */
	private static int modsToLoad = 1;
	private static int loadedMods = 0;
	private static int filesToLoad = 1;
	private static int loadedFiles = 0;

	/**
	 * Is set to true when the parser has finished loading the mods.
	 */
	private static boolean finished = false;

	/**
	 * This method returns the mods contained in the file "loadorder.txt".
	 * @return array with mod-folder-names to load
	 */
	public static String[] getModList () {
		String[] mods = new String[0];

		File modListFile = new File("data/loadorder.txt");
		if ((modListFile != null) && (modListFile.exists())) {

			try {
				FileInputStream fileInputStream = new FileInputStream(modListFile);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				List<String> modList = new ArrayList<String>();
				String line;

				while ((line = bufferedReader.readLine()) != null) {
					modList.add(line);
				}

				if (modList.size() > 0) {
					mods = new String[modList.size()];
					for (int i=0 ; i<modList.size() ; i++) { mods[i] = modList.get(i); }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return mods;
	}

	/**
	 * Main entry for mod loading.
	 * @param mods to load
	 */
	public static void loadMods (String[] mods) {
		if (mods == null) return;
		modsToLoad = mods.length;

		for (String mod : mods) {
			try {
				File modDirectory = new File("data/mods/"+mod+"/definitions");

				List<File> modFiles = getFilesFromDirectory(modDirectory);

				if ((modFiles != null) && (modFiles.size() > 0)) {
					filesToLoad = modFiles.size();
					for (File file : modFiles) {
						loadFile(file);
						loadedFiles++;
					}
				}
			} catch (SecurityException e) {
				System.out.println("Could not load mod '"+mod+"'. Missing permissions.");
				e.printStackTrace();
			}
			loadedMods++;
		}
		finished = true;
		Log.status();
	}

	/**
	 * Returns all the readable files from a directory.
	 */
	private static List<File> getFilesFromDirectory(File directory) {
		List<File> files = new ArrayList<>();

		String[] entries = directory.list();
		for(String entry : entries) {
			File file = new File(directory.getPath()+"/"+entry);
			if (file.exists()) {
				if (file.isDirectory()) {
					files.addAll(getFilesFromDirectory(file));
				} else {
					int dot = file.getName().lastIndexOf('.');
					if ((dot > 0) && (file.getName().substring(dot+1).equals("def"))) {
						files.add(file);
					}
				}
			}
		}

		return files;
	}

	/**
	 * Loads a certain file.
	 */
	private static void loadFile(File file) {
		Log.setCurrentFile(file.getPath()); // for better error retrieval
		Log.setCurrentLine(1);
		try {
			FileReader fileReader = new FileReader(file);

			try {

				String codeBuilder = "";
				char currentChar = 0;

				int blockCounter = 0;
				Deque<ScriptBlock> scriptBlockStack = new LinkedList<>();

				while (fileReader.ready()) {
					//System.out.print((char) fileReader.read());

					currentChar = (char) fileReader.read();

					if (currentChar == Sign.OPEN_BLOCK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% start of new block
						//System.out.println("startet block : "+codeBuilder);
						blockCounter++;
						scriptBlockStack.push(new ScriptBlock(codeBuilder.trim(),true));

						codeBuilder = "";
					} else if (currentChar == Sign.CLOSE_BLOCK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end of a block
						//System.out.println("ended block");
						blockCounter--;
						ScriptBlock scriptBlock = scriptBlockStack.pop();
						if (scriptBlockStack.peekFirst() != null) {
							//System.out.println("adds block to another");
							scriptBlockStack.peekFirst().addScriptBlock(scriptBlock);
						} else {
							//System.out.println("adds block to a new entity");
							//Entity entity = Data.getOrCreateEntity(scriptBlock.getExpression().getValue().getText());
							//entity.addScriptBlock(scriptBlock);
						}

						codeBuilder = "";
					} else if (currentChar == Sign.END_OF_BLOCK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end of a line
						//System.out.println("end of a line: "+codeBuilder);
						if (!codeBuilder.trim().equals("")) {
							scriptBlockStack.peekFirst().addScriptBlock(new ScriptBlock(codeBuilder, false));
						}

						codeBuilder = "";
					} else { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% a "normal" char
						if (currentChar >= 32) { // no special chars please
							codeBuilder = codeBuilder + currentChar;
						}
						if (currentChar == 10) Log.setCurrentLine(Log.getCurrentLine()+1);
					}
				}

				if (blockCounter != 0) {
					Log.error("Block brackets "+Sign.OPEN_BLOCK.getChar()+" and "+Sign.CLOSE_BLOCK.getChar()+" do not match up");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.out.println("Could not load file '"+file.getName()+"'");
			e.printStackTrace();
		}

	}

	/**
	 * Returns the loading progress as a double, ranging from 0 to 1.
	 */
	public static double getProgress() {
		if (modsToLoad < 1) modsToLoad = 1;
		if (filesToLoad < 1) filesToLoad = 1;
		double progress = ((double) loadedMods / (double) modsToLoad) + ((double) loadedFiles / (double) filesToLoad / (double) modsToLoad);
		return (progress>1?1:progress);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public static boolean isFinished() {
		return finished;
	}
	public static void setFinished(boolean finished) {
		Parser.finished = finished;
	}
}
