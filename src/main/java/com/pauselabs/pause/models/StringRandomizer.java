package com.pauselabs.pause.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mpollin on 11/7/14.
 */
public class StringRandomizer {
	private Context mContext;
	private Random mRandNumGenerator;
	private ArrayList<ArrayList<String>> mComponents;

	public StringRandomizer(Context context) {
		this(context, null);
	}

	public StringRandomizer(Context context, String filename) {
		mContext = context;
		mRandNumGenerator = new Random();
		mComponents = new ArrayList<ArrayList<String>>();

		try {
			parseFile(filename);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < mComponents.size(); i++) {
			ArrayList<String> component = mComponents.get(i);

			int index = mRandNumGenerator.nextInt(component.size());

			sb.append(component.get(index));

			if (i != mComponents.size())
				sb.append(' ');
		}

		return sb.toString();
	}

	public void setFile(String filename) {
		try {
			parseFile(filename);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseFile(String filename) throws JSONException, IOException {
		mComponents.clear();
		if (filename == null)
			return;

		InputStream is = mContext.getAssets().open(filename);
		int size = is.available();

		byte[] buffer = new byte[size];

		is.read(buffer);

		is.close();

		JSONArray in = new JSONArray(new String(buffer, "UTF-8"));
		for (int i = 0; i < in.length(); i++)
			parseComponent(in.getJSONArray(i));

	}

	private void parseComponent(JSONArray strings) throws JSONException {
		ArrayList<String> component = new ArrayList<String>();
		for (int i = 0; i < strings.length(); i++)
			component.add(strings.getString(i));

		mComponents.add(component);
	}
}
