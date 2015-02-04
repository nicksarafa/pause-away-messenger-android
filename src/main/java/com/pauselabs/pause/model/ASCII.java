package com.pauselabs.pause.model;

/**
 * Created by Passa on 2/4/15.
 */
public class ASCII {

    private String name;
    private String ascii;

    public ASCII(String name, String ascii) {
        this.name = name;
        this.ascii = ascii;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAscii() {
        return ascii;
    }

    public void setAscii(String ascii) {
        this.ascii = ascii;
    }
}
