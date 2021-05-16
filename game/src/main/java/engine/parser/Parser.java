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

	private List<String> mods;

	// ###################################################################################
	// ################################ Load Mods ########################################
	// ###################################################################################

	public void load() {
		mods = ModOrderLoader.loadMods();

		for (String mod : mods) {
			Logger.log("load mod: " + mod);
			loadMod(mod);
		}

		Data.prepareForGame();
		debug();
	}

	private void loadMod(String mod) {
		File definitionsFolder = new File(ResourcePathConstants.MOD_FOLDER + mod + "/" + ResourcePathConstants.DEFINITIONS_FOLDER);

		if (!definitionsFolder.exists() || !definitionsFolder.isDirectory()) {
			Logger.error("Mod '" + mod + "' does not have a '" + ResourcePathConstants.DEFINITIONS_FOLDER + "' folder!");
			return;
		}

		loadFolder(definitionsFolder, mod);
	}

	private void loadFolder(File folder, String mod) {
		for (String fileName : folder.list()) {
			File file = new File(folder.getAbsolutePath() + "/" + fileName);

			if (file.isFile() && file.getName().endsWith(ScriptConstants.SCRIPT_FILE_SUFFIX)) {
				Logger.log("load file: " + file.getName());
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

			Interpreter interpreter = new Interpreter(tokens, currentMod, file.getName());
			interpreter.interpret();

		} catch (FileNotFoundException e) {
			Logger.error("Could not find file: " + file.getPath());
			e.printStackTrace();

		} catch (Exception e) {
			Logger.error("Parsing error in file: " + file.getName());
			e.printStackTrace();
		}
	}

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
		while ((container = Data.getContainer(i++).orElse(null)) != null) {
			System.out.println("______________________________________________________________________");
			System.out.println(">>>>>>>>> " + container.getType() + ": " + container.getTextID() + ", " + container.getName(null));

			System.out.println(">>> Properties:");
			container.printProperties();

			System.out.println(">>> Stages:");
			for (String stage : container.getStages()) {
				System.out.println("Stage: " + stage);

				IDInterface[] attributes = container.getAttributes(stage).toArray();
				if (attributes != null) {
					for (IDInterface idInterface : attributes) {
						Attribute attribute = (Attribute) idInterface;
						System.out.println("   -> Att: " + attribute.getId() + " (" + Data.getProtoAttribute(attribute.getId()).getTextID() + ") " + ", " + attribute.getValue());
					}
				}

				if (container.getType() == DataType.CREATURE) {
					for (Container process : ((CreatureContainer) container).getKnowledge(stage)) {
						System.out.println("   -> Pro: " + process);
					}
					for (Container drive : ((CreatureContainer) container).getDrives(stage)) {
						System.out.println("   -> Dri: " + drive);
					}
				} else if (container.getType() == DataType.DRIVE) {
					for (Container solution : ((DriveContainer) container).getSolutions(stage)) {
						System.out.println("   -> Sol: " + solution);
					}
				} else if (container.getType() == DataType.PROCESS) {
					for (Container alternative : ((ProcessContainer) container).getSolutions(stage)) {
						System.out.println("   -> Sol: " + alternative);
					}
				}

				IDInterface[] scripts = container.getScripts(stage).toArray();
				if (scripts != null) {
					for (IDInterface idInterface : scripts) {
						Script script = (Script) idInterface;
						script.print();
					}
				}
			}
		}
	}

}
