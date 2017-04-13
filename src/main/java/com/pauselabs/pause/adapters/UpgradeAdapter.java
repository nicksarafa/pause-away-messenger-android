package com.pauselabs.pause.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.UpgradeListItem;
import javax.inject.Inject;

/** Created by Passa on 3/20/15. */
public class UpgradeAdapter extends ArrayAdapter<UpgradeListItem> {

  private int resource;

  @Inject LayoutInflater inflater;

  public UpgradeAdapter(Context context, int resource) {
    super(context, resource);

    Injector.inject(this);

    this.resource = resource;

    resetList();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final UpgradeListItem item = getItem(position);

    return item;
  }

  public void resetList() {
    clear();

    notifyDataSetChanged();
  }
}
