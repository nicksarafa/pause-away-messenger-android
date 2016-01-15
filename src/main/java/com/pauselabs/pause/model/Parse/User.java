package com.pauselabs.pause.model.Parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Passa on 3/20/15.
 */
@ParseClassName("_User")
public class User extends ParseUser {



    public static List<User> getAllUsers() throws ParseException {
        return ParseQuery.getQuery(User.class).find();
    }



}
