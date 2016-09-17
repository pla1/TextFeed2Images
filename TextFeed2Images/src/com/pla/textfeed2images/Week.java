package com.pla.textfeed2images;

public class Week {
  private boolean current = false;
  private String imageUrl;
  private String type;
  private int week;
  private int year;

  public String toString() {
    return String.format("%d %d %s %s %b", year, week, type, imageUrl, current);
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getType() {
    return type;
  }

  public int getWeek() {
    return week;
  }

  public int getYear() {
    return year;
  }

  public boolean isCurrent() {
    return current;
  }

  public void setCurrent(boolean current) {
    this.current = current;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setWeek(int week) {
    this.week = week;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
