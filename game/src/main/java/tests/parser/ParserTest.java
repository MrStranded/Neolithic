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
		Variable three = new Variable("threeVar");
		three.setDouble(-Math.PI);
		Variable four = new Variable("fourVar");
		four.setString(" swäg \"");

		Variable[] parameters = new Variable[4];
		parameters[0] = one;
		parameters[1] = two;
		parameters[2] = three;
		parameters[3] = four;

		instance.runScript("s4", parameters);

		instance.printVariables();

		instance.removeVariable("testVar");

		instance.printVariables();
	}

}
