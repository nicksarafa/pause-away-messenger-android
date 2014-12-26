package com.pauselabs.pause.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.pauselabs.pause.Injector;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * This class will construct a randomized String from a given JSON file.
 *
 * NOTE: The JSON file must be in the assets folder
 *
 * Created by mpollin on 11/7/14.
 */
public class StringRandomizer {

    @Inject
    SharedPreferences prefs;

    protected Context mContext;
    protected Random randNumGenerator;
    private ArrayList<ArrayList<String>> components;

	/**
	 * Construct a StringRandomizer without an initial JSON file
	 *
	 * NOTE: without a file this class is useless
	 *
	 * @param context
	 */
	public StringRandomizer(Context context) {
		this(context, null);
	}

	/**
	 * Construct a StringRandomizer with an initial JSON file
	 *
	 * @param c
	 * @param filename The name of the JSON file
	 */
	public StringRandomizer(Context c, String filename) {
        mContext = c;
        randNumGenerator = new Random();
        components = new ArrayList<ArrayList<String>>();

        Injector.inject(this);

        try {
            parseFile(filename);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * @return A randomized String generated from the given JSON file.
	 * 		Returns null if there is no JSON file.
	 */
	public String getString(String contactName) {
		if (components.isEmpty())
			return null;

        Pattern contactPattern = Pattern.compile("%contact");
        Pattern namePattern = Pattern.compile("%name");
        Pattern genderPattern = Pattern.compile("%gender");
        Matcher m;

        boolean isMale = prefs.getBoolean(Constants.Settings.IS_MALE, true);
        String genderValue;
        if (isMale)
            genderValue = Constants.Settings.GENDER_MALE_VALUE;
        else
            genderValue = Constants.Settings.GENDER_FEMALE_VALUE;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < components.size(); i++) {
			ArrayList<String> component = components.get(i);

			int index = randNumGenerator.nextInt(component.size());
            String text = component.get(index);

            m = contactPattern.matcher(text);
            text = m.replaceAll(contactName);

            m = namePattern.matcher(text);
            text = m.replaceAll(prefs.getString(Constants.Settings.NAME_KEY,""));

            m = genderPattern.matcher(text);
            text = m.replaceAll(genderValue);

            sb.append(text);

			if (i != components.size())
				sb.append(' ');
		}

		return sb.toString();
	}

    /**
     * Set the JSON file to be used for generating Strings
     *
     * @param filename
     */
    public void setFile(String filename) {
        try {
            parseFile(filename);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void parseFile(String filename) throws JSONException, IOException {
        components.clear();
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

    public void parseComponent(JSONArray strings) throws JSONException {
        ArrayList<String> component = new ArrayList<String>();
        for (int i = 0; i < strings.length(); i++)
            component.add(strings.getString(i));

        this.components.add(component);
    }
}
