package com.pla.textfeed2images;

public class Item {
  private String link;
  private String description;
  private String title;
  private String dateDisplay;

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(title).append(" ");
    sb.append(description).append(" ");
    sb.append(dateDisplay);
    return sb.toString();
  }

  public Item() {
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDateDisplay() {
    return dateDisplay;
  }

  public void setDateDisplay(String dateDisplay) {
    this.dateDisplay = dateDisplay;
  }

}
