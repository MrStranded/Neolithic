package tests.parser;

import engine.Engine;
import engine.data.entities.Instance;
import engine.data.proto.Data;
import engine.data.variables.Variable;
import engine.parser.interpretation.Interpreter;

public class ParserTest {

	public static void main(String[] args) {
		Engine.initialize();
		Engine.loadData();
		Engine.cleanUp();
	}

}
