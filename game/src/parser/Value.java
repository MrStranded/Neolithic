package parser;

import enums.script.Command;
import enums.script.DataType;
import environment.world.Entity;

/**
 * Created by Michael on 11.09.2017.
 */
public class Value {

	private DataType dataType = null;

	private String text = "";
	private double number = 0d;
	private Entity object = null;
	private Command command = null;
	private Variable variable = null;

	public Value (DataType dataType, String text) {
		this.dataType = dataType;
		this.text = text;
	}
	public Value (DataType dataType, double number) {
		this.dataType = dataType;
		this.number = number;
	}
	public Value (DataType dataType, Entity object) {
		this.dataType = dataType;
		this.object = object;
	}
	public Value (DataType dataType, Command command) {
		this.dataType = dataType;
		this.command = command;
	}
	public Value (DataType dataType, Variable variable) {
		this.dataType = dataType;
		this.variable = variable;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public DataType getDataType() {
		return dataType;
	}

	public String getText() {
		return text;
	}

	public double getNumber() {
		return number;
	}

	public Entity getObject() {
		return object;
	}

	public Command getCommand() {
		return command;
	}

	public Variable getVariable() {
		return variable;
	}

	public String toString() {
		String str = "";
		if (dataType!=null) str += dataType.name() + "|";
		switch (dataType) {
			case TEXT: case EVENT:
				str += text;
				break;
			case NUMBER:
				str += String.valueOf(number);
				break;
			case OBJECT:
				if (object != null) str += object.getTextID();
				break;
			case COMMAND:
				if (command != null) str += command.toString();
				break;
			case VARIABLE:
				if (variable != null) {
					str += variable.toString();
				} else {
					str += "?"+text+"?"; // a value that did not yet find a variable but knows the name already
				}
				break;
		}
		return str;
	}

}
