package com.pla.textfeed2images;

import java.util.Date;

public class Game implements Comparable<Game> {
  private String homeTeam;
  private int homePoints;
  private String eventId;
  private String possession;
  private String dayOfWeek;
  private String gameTime;
  private String redZone;
  private String scoringMethod;
  private String quarter;
  private String timeRemaining;
  private String type;
  private int week;
  private int year;
  private String awayTeam;
  private int awayPoints;
  private Date date;
  private boolean finished = false;

  public Game() {
  }

  public String getImageFileName() {
    StringBuilder fileName = new StringBuilder();
    fileName.append("NFL");
    fileName.append(year);
    if (week < 10) {
      fileName.append("0");
    }
    fileName.append(week);
    fileName.append(type);
    fileName.append(".png");
    return fileName.toString();
  }

  private boolean isPending() {
    return "P".equals(quarter);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(awayTeam);
    if (!isPending()) {
      sb.append(" ");
      sb.append(awayPoints);
    }
    sb.append(" at ");
    sb.append(homeTeam);
    if (!isPending()) {
      sb.append(" ");
      sb.append(homePoints);
    }
    if (isPending()) {
      sb.append(" ");
      sb.append(gameTime);
      sb.append(" ");
      sb.append(getDayOfWeekDisplay());
    }
    return sb.toString();
  }

  public String getDayOfWeekDisplay() {
    if (dayOfWeek == null || dayOfWeek.length() < 4) {
      return dayOfWeek;
    }
    return dayOfWeek.substring(0, 3);
  }

  public String getHomeTeam() {
    return homeTeam;
  }

  public void setHomeTeam(String homeTeam) {
    this.homeTeam = homeTeam;
  }

  public int getHomePoints() {
    return homePoints;
  }

  public void setHomePoints(int homePoints) {
    this.homePoints = homePoints;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getPossession() {
    return possession;
  }

  public void setPossession(String possession) {
    this.possession = possession;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getGameTime() {
    return gameTime;
  }

  public void setGameTime(String gameTime) {
    this.gameTime = gameTime;
  }

  public String getRedZone() {
    return redZone;
  }

  public void setRedZone(String redZone) {
    this.redZone = redZone;
  }

  public String getScoringMethod() {
    return scoringMethod;
  }

  public void setScoringMethod(String scoringMethod) {
    this.scoringMethod = scoringMethod;
  }

  public String getQuarter() {
    return quarter;
  }

  public void setQuarter(String quarter) {
    this.quarter = quarter;
  }

  public String getTimeRemaining() {
    return timeRemaining;
  }

  public void setTimeRemaining(String timeRemaining) {
    this.timeRemaining = timeRemaining;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getWeek() {
    return week;
  }

  public void setWeek(int week) {
    this.week = week;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public String getAwayTeam() {
    return awayTeam;
  }

  public void setAwayTeam(String awayTeam) {
    this.awayTeam = awayTeam;
  }

  public int getAwayPoints() {
    return awayPoints;
  }

  public void setAwayPoints(int awayPoints) {
    this.awayPoints = awayPoints;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (homeTeam == null) {
      return false;
    }
    Game game = (Game) obj;
    if (game.getHomeTeam() == null) {
      return false;
    }
    return super.equals(obj);
  }

  @Override
  public int compareTo(Game game) {
    if (homeTeam == null) {
      return -1;
    }
    if (game.getHomeTeam() == null) {
      return 1;
    }
    return homeTeam.compareTo(game.getHomeTeam());
  }

}
