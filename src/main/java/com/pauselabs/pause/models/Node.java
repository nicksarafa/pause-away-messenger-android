package com.pauselabs.pause.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.JarException;

/**
 * First attempt at a creating a Node.
 */
public class Node {
	private int id;
	private String type;
	private String content;
	private ArrayList<Integer> options;
	private ArrayList<String> choices;

	public Node(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		type = obj.getString("type");
		content = obj.getString("content");

		JSONArray jOptions = obj.getJSONArray("options");
		parseNormalOptions(jOptions);
		if (isSelectOne()) {
			parseStringOptions(obj.getJSONArray("choices"));
		}

	}

	private void parseNormalOptions(JSONArray obj) throws JSONException{
		options = new ArrayList<Integer>();
		for (int i = 0; i < obj.length(); i++) {
			options.add(obj.getInt(i));
		}
	}

	private void parseStringOptions(JSONArray obj) throws JSONException {
		choices = new ArrayList<String>();
		for (int i = 0; i < obj.length(); i++) {
			choices.add(obj.getString(i));
		}
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public ArrayList<Integer> getOptions() {
		return options;
	}

	public ArrayList<String> getChoices() { return choices; }

	public boolean isNormal() { return type.equals("normal"); }

	public boolean isSelectOne() { return type.equals("select_one"); }

	public boolean isUserInput() { return type.equals("user_input"); }

	public String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		String indent2 = indent + "\t";

		sb.append(indent).append("{\n");

		sb.append(indent2).append("id : ").append(id).append(",\n")
				.append(indent2).append("type : ").append(type).append(",\n")
				.append(indent2).append("content : ").append(content).append(",\n")
				.append(indent2).append("options : [");
		//for (Integer option : options) {
		//	sb.append(option).append(",");
		//}
		sb.append("]\n");
		sb.append(indent).append("}\n");

		return sb.toString();
	}

}
