package tests.parser;

import engine.Engine;

public class ParserTest {

	public static void main(String[] args) {
		Engine.initialize();
		Engine.loadData();
		Engine.cleanUp();
	}

}
