package engine.parser;

import load.ModLoader;

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

	public void load() {
		mods = ModLoader.loadMods();

		for (String mod : mods) {
			System.out.println(mod);
		}
	}

	public void parse() {

	}

}
