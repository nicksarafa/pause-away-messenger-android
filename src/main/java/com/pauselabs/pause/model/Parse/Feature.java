package com.pauselabs.pause.model.Parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Passa on 3/20/15.
 */
@ParseClassName("Feature")
public class Feature extends ParseObject {

    private final String ICON_TEXT_KEY = "icon_text";
    private final String NAME_KEY = "name";
    private final String DESC_KEY = "desc";
    private final String VOTERS_KEY = "voters";

    public static List<Feature> getAllFeatures() {
        ParseQuery<Feature> query = ParseQuery.getQuery(Feature.class);
        List<Feature> features = null;

        try {
            features = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return features;
    }

    public String getIconText() {
        return getString(ICON_TEXT_KEY);
    }
    public void setIconText(String iconText) {
        put(ICON_TEXT_KEY, iconText);
    }

    public String getName() {
        return getString(NAME_KEY);
    }
    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public String getDescription() {
        return getString(DESC_KEY);
    }
    public void setDescription(String description) {
        put(DESC_KEY, description);
    }

    public ParseRelation<User> getVotersRelation() {
        return getRelation(VOTERS_KEY);
    }
    public void addVoter(User voter) {
        getVotersRelation().add(voter);
    }
    public void removeVoter(User voter) {
        getVotersRelation().remove(voter);
    }

}
