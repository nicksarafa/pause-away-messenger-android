package com.pauselabs.pause.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.ParseException;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Parse.Feature;
import com.pauselabs.pause.model.Parse.User;
import com.pauselabs.pause.view.UpgradeListItem;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Passa on 3/20/15.
 */
public class UpgradeAdapter extends ArrayAdapter<UpgradeListItem> {

    @Inject
    LayoutInflater inflater;

    public UpgradeAdapter(Context context, int resource) {
        super(context, resource);

        Injector.inject(this);

        for (Feature feature : PauseApplication.parseVars.features) {
            UpgradeListItem item = (UpgradeListItem)inflater.inflate(resource, null);
            item.setTag(feature);

            add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UpgradeListItem item = getItem(position);
        Feature feature = (Feature)item.getTag();

        item.iconText.setText(feature.getIconText() + " " + feature.getName());

        try {
            feature = feature.fetchIfNeeded();

            boolean isVoter = false;
            List<User> voters = feature.getVotersRelation().getQuery().find();
            for (User voter : voters) {
                if (voter.getObjectId().equals(PauseApplication.parseVars.currentUser.getObjectId())) {
                    isVoter = true;
                }
            }

            if (isVoter)
                item.setBackgroundColor(Color.GREEN);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return item;
    }

}
