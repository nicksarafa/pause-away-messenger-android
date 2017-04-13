package com.pauselabs.pause.model;

import org.json.JSONException;
import org.json.JSONObject;

/** Created by mpollin on 10/12/14. */
public class Option {
  private int id;
  private String type;
  private String content;
  private int nextNode;

  public Option(JSONObject obj) throws JSONException {
    id = obj.getInt("id");
    type = obj.getString("type");
    content = obj.getString("content");
    nextNode = obj.getInt("next_node");
  }

  public int getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getContent() {
    return content;
  }

  public int getNextNode() {
    return nextNode;
  }

  public String toString(String indent) {
    StringBuilder sb = new StringBuilder();
    String indent2 = indent + "\t";

    sb.append(indent)
        .append("{\n")
        .append(indent2)
        .append("id : ")
        .append(id)
        .append(", \n")
        .append(indent2)
        .append("type : ")
        .append(type)
        .append(", \n")
        .append(indent2)
        .append("content : ")
        .append(content)
        .append(", \n")
        .append(indent2)
        .append("next_node : ")
        .append(nextNode)
        .append("\n")
        .append(indent)
        .append("}\n");

    return sb.toString();
  }
}
