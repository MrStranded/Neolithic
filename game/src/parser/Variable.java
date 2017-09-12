package parser;

import enums.script.DataType;

/**
 * Variables may store arrays of data.
 *
 * Created by Michael on 11.09.2017.
 */
public class Variable {

	private String name = null;

	private Value value = null;
	private Variable[] array = null;

	public Variable(String name,Expression expression) {
		this.name = name;

		if (expression.getArray() != null) {
			array = new Variable[expression.getArray().size()];
			int i=0;
			for (Expression exp : expression.getArray()) {
				array[i] = new Variable(null,exp);
				i++;
			}
		} else {
			value = expression.getValue();
		}
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public String getName() {
		return name;
	}

	public Value getValue() {
		return getValue(new int[0],0);
	}
	public Value getValue(int pos) {
		int[] position = {pos};
		return getValue(position);
	}
	public Value getValue(int[] pos) {
		return getValue(pos,0);
	}
	public Value getValue(int[] pos,int current) {
		if (pos == null) {
			System.out.println("ERROR: tried to access array field with null position.");
			return null;
		}
		if (current < pos.length) {
			if ((array != null) && (array.length > pos[current])) {
				return array[pos[current]].getValue(pos,current++);
			} else {
				String position = ""+pos[0];
				for (int p=1; p<pos.length; p++) {position += ","+pos[p];}
				System.out.println("ERROR: tried accessing inexistent array field ["+position+"] in variable: "+name);
				return null;
			}
		} else {
			return value;
		}
	}

	public String toString() {
		String str = "";
		if (array != null) {
			str += "[";
			for (Variable var : array) {
				str += var.toString();
			}
			str += "]";
		} else {
			str = getValue().toString();
		}
		return str;
	}

}
