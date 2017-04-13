package com.pauselabs.pause.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.pauselabs.pause.Injector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

/** Created by Passa on 12/17/14. */
public class JsonReader {

  @Inject SharedPreferences prefs;

  protected Context context;
  protected Random randNumGenerator;
  protected JSONObject object;

  public JsonReader(Context c, String filename) {
    context = c;
    randNumGenerator = new Random();

    Injector.inject(this);

    try {
      parseFile(filename);
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public JSONObject getObject() {
    return object;
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
    if (filename == null) return;

    InputStream is = context.getAssets().open(filename);
    int size = is.available();

    byte[] buffer = new byte[size];

    is.read(buffer);

    is.close();

    object = new JSONObject(new String(buffer, "UTF-8"));
  }
}
