package engine.parser;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import load.ModOrderLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

	private double progress = 0; // ranging from 0 to 1

	private List<String> mods;
	private List<CodeBlock> blocks;
	private Stack<CodeBlock> blockStack;

	public Parser() {
		blocks = new ArrayList<>(16);
		blockStack = new Stack<>();
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
				System.out.println("load file: " + file.getName());
				try {
					Tokenizer.tokenize(new FileReader(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
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
