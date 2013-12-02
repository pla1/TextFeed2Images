package com.pla.textfeed2images;

public class Team {
  private String teamId;
  private String name;
  private String city;
  private boolean found = false;

  public Team() {
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(teamId).append(" ");
    sb.append(city).append(" ");
    sb.append(name).append(" ");
    return sb.toString();
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public boolean isFound() {
    return found;
  }

  public void setFound(boolean found) {
    this.found = found;
  }

}
