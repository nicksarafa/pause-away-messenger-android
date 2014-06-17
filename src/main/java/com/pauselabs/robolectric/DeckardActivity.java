package com.pauselabs.robolectric;

import android.app.Activity;
import android.os.Bundle;
import com.pauselabs.R;

public class DeckardActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.deckard);
  }
}
