package engine.parser;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.proto.*;
import engine.data.scripts.Script;
import engine.data.variables.DataType;
import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;
import engine.parser.tokenization.Tokenizer;
import engine.parser.utils.Logger;
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

		Data.prepareForGame();
	}

	private void loadMod(String mod) {
		File definitionsFolder = new File(ResourcePathConstants.MOD_FOLDER + mod + "/" + ResourcePathConstants.DEFINITIONS_FOLDER);

		if (!definitionsFolder.exists() || !definitionsFolder.isDirectory()) {
			Logger.error("Mod '" + mod + "' does not have a 'definitions' folder!");
			return;
		}

		loadFolder(definitionsFolder, mod);
	}

	private void loadFolder(File folder, String mod) {
		for (String fileName : folder.list()) {
			File file = new File(folder.getAbsolutePath() + "/" + fileName);

			if (file.isFile() && file.getName().endsWith(ScriptConstants.SCRIPT_FILE_SUFFIX)) {
				System.out.println("load file: " + file.getName());
				loadFile(file, mod);
			} else if (file.isDirectory()) {
				loadFolder(file, mod);
			}
		}
	}

	// ###################################################################################
	// ################################ Load Files #######################################
	// ###################################################################################

	private void loadFile(File file, String currentMod) {
		try {
			List<Token> tokens = Tokenizer.tokenize(new FileReader(file));

			try {
				Interpreter interpreter = new Interpreter(tokens, currentMod, file.getName());
				interpreter.interpret();
			} catch (Exception e) {
				Logger.error("Parsing error in file: " + file.getName());
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// ###################################################################################
	// ################################ Parse ############################################
	// ###################################################################################

	public void parse() {

	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public void debug() {
		System.out.println("/////////////////////////////////////////////////////////////");
		int i = 0;
		ProtoAttribute protoAttribute;
		while ((protoAttribute = Data.getProtoAttribute(i++)) != null) {
			System.out.println("ProtoAttribute: " + protoAttribute.getTextID() + ", " + protoAttribute.getName());
		}

		i = 0;
		Container container;
		while ((container = Data.getContainer(i++)) != null) {
			System.out.println(container.getType() + ": " + container.getTextID() + ", " + container.getName());

			IDInterface[] attributes = container.getAttributes();
			if (attributes != null) {
				for (IDInterface idInterface : attributes) {
					Attribute attribute = (Attribute) idInterface;
					System.out.println("-> Att: " + attribute.getId() + " (" + Data.getProtoAttribute(attribute.getId()).getTextID() + ") " + ", " + attribute.getValue());
				}
			}

			if (container.getType() == DataType.CREATURE) {
				for (ContainerIdentifier process : ((CreatureContainer) container).getKnowledge()) {
					System.out.println("-> Pro: " + process);
				}
				for (ContainerIdentifier drive : ((CreatureContainer) container).getDrives()) {
					System.out.println("-> Dri: " + drive);
				}
			} else if (container.getType() == DataType.DRIVE) {
				for (ContainerIdentifier solution : ((DriveContainer) container).getSolutions()) {
					System.out.println("-> Sol: " + solution);
				}
			} else if (container.getType() == DataType.PROCESS) {
				for (ContainerIdentifier alternative : ((ProcessContainer) container).getSolutions()) {
					System.out.println("-> Sol: " + alternative);
				}
			}

			IDInterface[] scripts = container.getScripts();
			if (scripts != null) {
				for (IDInterface idInterface : scripts) {
					Script script = (Script) idInterface;
					script.print();
				}
			}
		}
	}

}
