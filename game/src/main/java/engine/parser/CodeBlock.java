package engine.parser;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock {

	private String type;
	private String name;

	List<CodeLine> lines;
	List<CodeBlock> blocks;

	public CodeBlock(String type, String name) {
		this.type = type;
		this.name = name;

		lines = new ArrayList<>(8);
		blocks = new ArrayList<>(2);
	}

	public void addLine(String line) {
		lines.add(new CodeLine(line));
	}

	public void addBlock(CodeBlock block) {
		blocks.add(block);
	}
}
