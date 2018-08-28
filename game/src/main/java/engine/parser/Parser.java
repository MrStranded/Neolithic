package engine.parser;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.data.proto.ProtoAttribute;
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
				if (file.getName().equals("smallGame.neo")) { // <- this right here
					System.out.println("load file: " + file.getName());
					loadFile(file);
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Load Files #######################################
	// ###################################################################################

	private void loadFile(File file) {
		try {
			List<Token> tokens = Tokenizer.tokenize(new FileReader(file));

			try {
				Interpreter interpreter = new Interpreter(tokens);
				interpreter.interpret();
			} catch (Exception e) {
				Logger.error("Parsing error in file: " + file.getName());
				e.printStackTrace();
			}

			int i = 0;
			ProtoAttribute protoAttribute;
			while ((protoAttribute = Data.getProtoAttribute(i++)) != null) {
				System.out.println("ProtoAttribute: " + protoAttribute.getTextID() + ", " + protoAttribute.getName());
			}
			i = 0;
			Container container;
			while ((container = Data.getContainer(i++)) != null) {
				System.out.println("Container: " + container.getTextID() + ", " + container.getName());
				IDInterface[] attributes = container.getAttributes();
				if (attributes != null) {
					for (IDInterface idInterface : attributes) {
						Attribute attribute = (Attribute) idInterface;
						System.out.println("-> Att: " + attribute.getId() + " (" + Data.getProtoAttribute(attribute.getId()).getTextID() + ") " + ", " + attribute.getValue());
					}
				}
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

}
