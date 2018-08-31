package tests.parser;

import engine.Engine;
import engine.data.entities.Instance;
import engine.data.proto.Data;
import engine.data.variables.Variable;
import engine.parser.interpretation.Interpreter;

public class ParserTest {

	public static void main(String[] args) {
		Data.initialize();
		Engine.loadData();

		Instance instance = new Instance(Data.getContainerID("eS"));
		instance.printVariables();
		instance.addVariable(new Variable("testVar"));
		instance.printVariables();

		Variable one = new Variable("oneVar");
		one.setDouble(10d);
		Variable two = new Variable("twoVar");
		two.setString("yolo!");

		Variable[] parameters = new Variable[2];
		parameters[0] = one;
		parameters[1] = two;

		instance.runScript("s4", parameters);

		instance.printVariables();
	}

}
