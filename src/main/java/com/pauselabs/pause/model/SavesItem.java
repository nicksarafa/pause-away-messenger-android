package com.pauselabs.pause.model;

/** Created by Passa on 2/4/15. */
public class SavesItem {

  private int id;
  private String text;

  public SavesItem(int id, String text) {
    this.id = id;

    this.text = text;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
