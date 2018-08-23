package engine.parser;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;
import engine.parser.tokenization.Tokenizer;
import load.ModOrderLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Parser {

	private double progress = 0; // ranging from 0 to 1

	private List<String> mods;

	public Parser() {
	}

	// ###################################################################################
	// ################################ Load Mods ########################################
	// ###################################################################################

	public void load() {
		mods = ModOrderLoader.loadMods();

		for (String mod : mods) {
			System.out.println("load mod: " + mod);
			loadMod(mod);
		}
	}

	private void loadMod(String mod) {
		File definitionsFolder = new File(ResourcePathConstants.MOD_FOLDER + mod + "/" + ResourcePathConstants.DEFINITIONS_FOLDER);

		if (!definitionsFolder.exists() || !definitionsFolder.isDirectory()) {
			Logger.error("Mod '" + mod + "' does not have a 'definitions' folder!");
			return;
		}

		for (File file : definitionsFolder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(ScriptConstants.SCRIPT_FILE_SUFFIX)) {
				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Big Temporary Hack
				if (file.getName().equals("smallestGame.neo")) { // <- this right here
					System.out.println("load file: " + file.getName());
					try {
						List<Token> tokens = Tokenizer.tokenize(new FileReader(file));

						try {
							Interpreter interpreter = new Interpreter(tokens);
							interpreter.interpret();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Load Files #######################################
	// ###################################################################################



	// ###################################################################################
	// ################################ Parse ############################################
	// ###################################################################################

	public void parse() {

	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

}
