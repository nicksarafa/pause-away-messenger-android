package com.pauselabs.pause.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.SavesDatabaseHelper;
import com.pauselabs.pause.model.SavesItem;
import com.pauselabs.pause.view.SavesView;
import javax.inject.Inject;

/** Created by Passa on 3/20/15. */
public class SavesAdapter extends ArrayAdapter<SavesView> {

  SavesDatabaseHelper dbHelper;

  private int resource;

  @Inject LayoutInflater inflater;

  public SavesAdapter(Context context, int resource) {
    super(context, resource);

    Injector.inject(this);

    dbHelper = new SavesDatabaseHelper(context);

    this.resource = resource;

    resetList();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    SavesView savesView = getItem(position);

    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(savesView.savesTextView.getLayoutParams());

    Drawable iconSilence =
        new IconDrawable(getContext(), Iconify.IconValue.fa_bell_slash_o)
            .colorRes(R.color.text_white)
            .sizeDp(22);
    Drawable iconInfo =
        new IconDrawable(getContext(), Iconify.IconValue.fa_question_circle)
            .colorRes(R.color.text_white)
            .sizeDp(22);

    SavesItem item = (SavesItem) savesView.getTag();
    if (dbHelper.isDefaultSave(item.getId())) {
      savesView.savesTextView.getBackground();
      savesView.savesTextView.setBackgroundResource(R.drawable.card_bg_save_default);
      savesView.savesTextView.setTextColor(
          PauseApplication.pauseActivity.getResources().getColor(R.color.text_white));
      params.setMargins(18, 4, 18, 4);
      savesView.savesTextView.setPadding(28, 20, 28, 20);
      savesView.savesTextView.getCompoundDrawablePadding();
      savesView.savesTextView.setCompoundDrawablePadding(28);
      savesView.savesTextView.setCompoundDrawables(iconSilence, null, iconSilence, null);
      savesView.savesTextView.setText(item.getText());
    }

    return savesView;
  }

  public void resetList() {
    clear();

    Cursor saves = dbHelper.getAllSaves();

    if (saves.getCount() > 0) {
      do {
        int id = saves.getInt(SavesDatabaseHelper.KEY_ID);
        String text = saves.getString(SavesDatabaseHelper.KEY_TEXT);
        SavesItem item = new SavesItem(id, text);

        SavesView savesView = (SavesView) inflater.inflate(resource, null);
        savesView.savesTextView.setText(item.getText());
        savesView.setTag(item);

        add(savesView);
      } while (saves.moveToNext());
    }

    notifyDataSetChanged();
  }
}
