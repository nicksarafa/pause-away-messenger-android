package com.pauselabs.pause.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.pauselabs.pause.Injector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Passa on 12/17/14.
 */
public class ComponentRandomizer {

    @Inject
    SharedPreferences prefs;

    protected Context context;
    protected Random randNumGenerator;
    protected ArrayList<JSONObject> components;

    public ComponentRandomizer(Context c, String filename) {
        context = c;
        randNumGenerator = new Random();
        components = new ArrayList<JSONObject>();

        Injector.inject(this);

        try {
            parseFile(filename);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getComponent() {
        if (components.isEmpty())
            return null;

        int index = randNumGenerator.nextInt(components.size());

        return components.get(index);
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

        InputStream is = context.getAssets().open(filename);
        int size = is.available();

        byte[] buffer = new byte[size];

        is.read(buffer);

        is.close();

        JSONArray in = new JSONArray(new String(buffer, "UTF-8"));
        for (int i = 0; i < in.length(); i++)
            components.add(in.getJSONObject(i));

    }
}
