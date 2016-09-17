package com.pla.textfeed2images;

import java.util.ArrayList;
import java.util.Calendar;

public class WeekDAO {

  public static void main(String[] args) {
    WeekDAO dao = new WeekDAO();
    System.out.println(dao.getStartOfCurrentWeek().getTime().toString());
    System.out.println(dao.getEndOfCurrentWeek().getTime().toString());
    ArrayList<Week> weeks = dao.get();
    for (Week week : weeks) {
      System.out.println(week);
    }
    System.out.println(weeks.size() + " weeks");
  }

  private Calendar getEndOfCurrentWeek() {
    Calendar calendar = Calendar.getInstance();
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
      calendar.add(Calendar.DAY_OF_YEAR, +1);
    }
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return calendar;
  }

  private Calendar getStartOfCurrentWeek() {
    Calendar calendar = Calendar.getInstance();
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
      calendar.add(Calendar.DAY_OF_YEAR, -1);
    }
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar;
  }

  public ArrayList<Week> get() {
    GameDAO gameDAO = new GameDAO();
    ArrayList<Game> games = gameDAO.getGames();
    ArrayList<Week> weeks = new ArrayList<Week>();
    int year = 0;
    int nflWeek = 0;
    String type = "";
    Calendar endOfWeek = getEndOfCurrentWeek();
    Calendar startOfWeek = getStartOfCurrentWeek();
    for (Game game : games) {
      if (year != game.getYear() || nflWeek != game.getWeek() || !type.equals(game.getType())) {
        Week week = new Week();
        week.setWeek(game.getWeek());
        week.setYear(game.getYear());
        week.setCurrent(game.getDate().after(startOfWeek.getTime()) && game.getDate().before(endOfWeek.getTime()));
        week.setType(game.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("/images/NFL");
        sb.append(week.getYear());
        if (week.getWeek() < 10) {
          sb.append("0");
        }
        sb.append(week.getWeek());
        sb.append(week.getType());
        sb.append(".png");
        week.setImageUrl(sb.toString());
        if (week.isCurrent()) {
          week.setImageUrl("/NFLCurrentScoresImageServlet.png");
        }
        weeks.add(week);
      }
      year = game.getYear();
      nflWeek = game.getWeek();
      type = game.getType();
    }
    return weeks;
  }

}
