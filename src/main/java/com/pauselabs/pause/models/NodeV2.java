package com.pauselabs.pause.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Trying a different model for the Nodes
 */
public class NodeV2 {
	private int id;
	private String type;
	private String content;
	private ArrayList<OptionV2> options;
	private ArrayList<String> choices;
	int variable = -1;

	public NodeV2(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		type = obj.getString("type");
		content = obj.getString("content");
		parseOptions(obj.getJSONArray("options"));
		if (obj.has("choices"))
			parseChoices(obj.getJSONArray("choices"));
		if (obj.has("variable"))
			variable = obj.getInt("variable");
	}

	private void parseOptions(JSONArray arr) throws JSONException {
		if (options == null)
			options = new ArrayList<OptionV2>();
		Log.d("NodeV2", arr.toString(4));
		for (int i = 0; i < arr.length(); i++) {
			Log.d("NodeV2", "Option " + i);
			options.add(new OptionV2(arr.getJSONObject(i)));
		}
	}

	private void parseChoices(JSONArray arr) throws JSONException {
		if (choices == null)
			choices = new ArrayList<String>();
		for (int i = 0; i < arr.length(); i++)
			choices.add(arr.getString(i));
	}

	public int getId() { return id; }
	public String getContent() { return content; }
	public ArrayList<OptionV2> getOptions() { return options; }
	public ArrayList<String> getChoices() { return choices; }
	public int getVariable() { return variable; }

	public boolean hasVariable() { return variable > 0; }
	public boolean isNormal() { return type.equals("normal"); }
	public boolean isUserInput() { return type.equals("user_input"); }
	public boolean isSelectOne() { return type.equals("select_one"); }

	public OptionV2 getDefault() {
		for (OptionV2 option : options) {
			if (option.isDefault())
				return option;
		}
		return null;
	}


	public final class OptionV2 {
		private String content;
		private String action;
		private boolean visible;
		private boolean def;
		private int next;

		public OptionV2(JSONObject obj) throws JSONException {
			content = obj.getString("content");
			Log.d("OptionV2", content);
			action = obj.getString("action");
			Log.d("OptionV2", action);
			visible = obj.getBoolean("visible");
			def = obj.getBoolean("default");
			next = obj.getInt("next");
			Log.d("OptionV2", next + "");
		}


		public String getContent() { return content; }

		public String getAction() { return action; }

		public boolean isVisible() { return visible; }

		public boolean isDefault() { return def; }

		public int getNext() { return next; }
	}
	/*
	{
			"id" : 1,
			"type" : "normal",
			"content" : "Hey I'm Pause! What's your name",
			"options" : [
				{
					"content" : "My name is...",
					"action" : "",
					"visible" : true,
					"default" : true,
					"next" : 2
				},
				{
					"content" : "Why do you want my name?",
					"action" : "",
					"visible" : true,
					"default" : false,
					"next" : 3
				}
			]
		},
		{
			"id" : 2,
			"type" : "user_input",
			"content" : "Please enter your name",
			"options" : [
				{
					"content" : "",
					"action" : "",
					"visible" : false,
					"default" : true,
					"next" : 5
				}
			],
			"variable" : 1000
		},
		{
			"id" : 3,
			"type" : "normal",
			"content" : "We want your name because...",
			"options" : [
				{
					"content" : "Okay! Here's my name",
					"action" : "",
					"visible" : true,
					"default" : true,
					"next" : 2
				},
				{
					"content" : "No name. Just call me...",
					"action" : "",
					"visible" : true,
					"default" : false,
					"next" : 4
				}
			]
		},
		{
			"id" : 4,
			"type" : "select_one",
			"content" : "No name. just call me...",
			"options" : [
				{
					"content" : "",
					"action" : "",
					"visible" : false,
					"default" : true,
					"next" : 6
				}
			],
			"choices" : ["Boss", "Chief", "Other"],
			"variable" : 1000
		},
		{
			"id" : 5,
			"type" : "normal",
			"content" : "Is this your name?",
			"options" : [
				{
					"content" : "Yup, that's me!",
					"action" : "",
					"visible" : true,
					"default" : true,
					"next" : 10
				},
				{
					"content" : "That's not my name!",
					"action" : "",
					"visible" : true,
					"default" : false,
					"next" : 3
				}
			],
			"variable" : 1000
		},
		{
			"id" : 6,
			"type" : "normal",
			"content" : "Are you sure you'd like me to call you ",
			"options" : [
				{
					"content" : "Yup, that's me!",
					"action" : "",
					"visible" : true,
					"default" : true,
					"node" : 10
				},
				{
					"content" : "On second thought...",
					"action" : "",
					"visible" : true,
					"default" : false,
					"node" : 1
				}
			],
			"variable" : 1000
		}
	 */
}
