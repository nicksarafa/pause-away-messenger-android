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

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Passa on 3/20/15.
 */
public class UpgradeAdapter extends ArrayAdapter<UpgradeListItem> {

    private int resource;

    @Inject
    LayoutInflater inflater;

    public UpgradeAdapter(Context context, int resource) {
        super(context, resource);

        Injector.inject(this);

        this.resource = resource;

        resetList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UpgradeListItem item = getItem(position);
        Feature feature = (Feature)item.getTag();

        item.iconText.setText(feature.getIconText() + " " + feature.getName());

        feature.getVotersRelation().getQuery().findInBackground().continueWith(new Continuation<List<User>, Object>() {
            @Override
            public Object then(Task<List<User>> task) throws Exception {
                boolean isVoter = false;

                List<User> voters = task.getResult();
                for (User voter : voters) {
                    if (voter.getObjectId().equals(PauseApplication.parseVars.currentUser.getObjectId())) {
                        isVoter = true;
                    }
                }

                if (isVoter)
                    item.setBackgroundColor(Color.GREEN);

                return null;
            }
        });

        return item;
    }

    public void resetList() {
        clear();

        for (Feature feature : PauseApplication.parseVars.features) {
            UpgradeListItem item = (UpgradeListItem)inflater.inflate(resource, null);
            item.setTag(feature);

            add(item);
        }

        notifyDataSetChanged();
    }

}
