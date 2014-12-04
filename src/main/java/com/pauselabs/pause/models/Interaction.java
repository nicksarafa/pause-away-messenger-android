package com.pauselabs.pause.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class will provide the public API to use the interaction feature.
 * My intent is that you will supply it with a JSON file located in the assets folder
 * and this class will automatically parse it into Java objects.
 */
public class Interaction {
	private Context mContext;
	private ArrayList<NodeV2> mNodes;
	private HashMap<Integer, Object> mVariables;

	public Interaction(Context context, String filename) {
		mContext = context;
		mNodes = new ArrayList<NodeV2>();
		mVariables = new HashMap<Integer, Object>();
		try {
			parseFile(filename);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseFile(String filename) throws JSONException, IOException {
		InputStream is = mContext.getAssets().open(filename);
		int size = is.available();

		byte[] buffer = new byte[size];

		is.read(buffer);

		is.close();

		JSONObject in = new JSONObject(new String(buffer, "UTF-8"));

		JSONArray jNodes = in.getJSONArray("nodes");
		for (int i = 0; i < jNodes.length(); i++) {
			Log.d("Interaction", "Node " + i);
			mNodes.add(new NodeV2(jNodes.getJSONObject(i)));
		}

		JSONArray jVariables = in.getJSONArray("variables");
		for (int i = 0; i < jVariables.length(); i++) {
			mVariables.put(jVariables.getInt(i), null);
		}
	}

	public NodeV2 getStart() {
		return mNodes.get(0);
	}

	public NodeV2 getNode(int id) {
		for (int i = 0; i < mNodes.size(); i++) {
			if (mNodes.get(i).getId() == id)
				return mNodes.get(i);
		}
		return null;
	}

	public Object getVariable(int id) {
		return mVariables.get(id);
	}

	public boolean variableHasValue(int id) {
		return mVariables.get(id) != null;
	}

	public void setVariable(int id, Object value) {
		mVariables.remove(id);
		mVariables.put(id, value);
	}


	@Override
	public String toString() {
		/*StringBuilder sb = new StringBuilder();

		sb.append("nodes : [\n");
		for (int i = 0; i < mNodes.size(); i++) {
			sb.append(mNodes.get(i).toString("\t"));
		}
		sb.append("],\n");

		return sb.toString();*/
		return super.toString();
	}











}
