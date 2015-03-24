package com.pauselabs.pause.model.Parse;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Passa on 3/20/15.
 */
public class GlobalVars {

    public User currentUser;

    public List<Feature> features;

    public GlobalVars() {
        requeryFeatures();
    }

    public void setCurrentUser(ParseUser currentUser) {
        this.currentUser = (User)currentUser;
    }

    public void requeryFeatures() {
        features = Feature.getAllFeatures();
    }

}
