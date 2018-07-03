package main.data.proto;

import main.log.Log;

import java.util.List;

/**
 * Created by Michael on 11.09.2017.
 *
 * Used to store data by Containers.
 */
public class Value {

	private String name = "";
	private String[] data;

	public Value(String name, List<String> dataList) {
		this.name = name;

		data = new String[dataList.size()];
		int i = 0;
		for (String s : dataList) {
			data[i] = s;
			i++;
		}
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public String tryToGetString(int i) {
		if ((i>=0)&&(i<data.length)) {
			return data[i];
		}
		return null;
	}
	public int tryToGetInt(int i) {
		if ((i>=0)&&(i<data.length)) {
			try {
				return Integer.parseInt(data[i]);
			} catch (NumberFormatException e) {
				Log.error("Tried to get integer value from "+data[i]+" in value '"+name+"'");
			}
		}
		return 0;
	}

	public String getName() {
		return name;
	}

	public String[] getData() {
		return data;
	}
}
