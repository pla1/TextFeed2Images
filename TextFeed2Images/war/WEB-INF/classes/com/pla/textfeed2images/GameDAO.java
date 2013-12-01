package com.pla.textfeed2images;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GameDAO {
  private static final SimpleDateFormat hhmm = new SimpleDateFormat("h:mm");

  public static void main(String[] args) {
    GameDAO dao = new GameDAO();
    ArrayList<Game> games = dao.getGames();
    for (Game game : games) {
      System.out.println(game + " " + game.getDate());
    }
  }

  public GameDAO() {
  }

  private Game transfer(ResultSet rs) throws SQLException {
    Game game = new Game();
    game.setHomeTeam(rs.getString("home_team_name"));
    game.setAwayTeam(rs.getString("away_team_name"));
    game.setHomePoints(rs.getInt("home_score"));
    game.setAwayPoints(rs.getInt("away_score"));
    game.setGameTime(hhmm.format(rs.getTimestamp("start_time")));
    game.setDayOfWeek(rs.getString("day_of_week"));
    if (rs.getBoolean("pending")) {
      game.setQuarter("P");
    }
    game.setWeek(rs.getInt("week"));
    game.setYear(rs.getInt("season_year"));
    game.setType(rs.getString("season_type"));
    game.setDate(rs.getDate("gamedate"));
    return game;
  }

  public ArrayList<Game> getGamesThisWeek() {
    int week = getCurrentWeek();
    return getGames(week);
  }

  public ArrayList<Game> getGames(int week) {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection
          .prepareStatement("select a.*, date(a.start_time) as gamedate,  b.name as home_team_name, c.name as away_team_name, start_time > current_timestamp as pending from game as a "
              + "join team as b on a.home_team = b.team_id join team as c on a.away_team = c.team_id where week = ? "
              + "order by season_type, start_time desc");
      ps.setInt(1, week);
      rs = ps.executeQuery();
      boolean done = false;
      int year = 0;
      while (rs.next() && !done) {
        int seasonYear = rs.getInt("season_year");
        if (year == 0) {
          year = seasonYear;
          games.add(transfer(rs));
        } else {
          if (year != seasonYear) {
            done = true;
          } else {
            games.add(transfer(rs));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return games;
  }

  public ArrayList<Game> getGames() {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select a.*, date(a.start_time) as gamedate, b.name as home_team_name, c.name as away_team_name, "
          + "start_time > current_timestamp as pending from game as a "
          + "join team as b on a.home_team = b.team_id join team as c on a.away_team = c.team_id "
          + "order by start_time desc");
      rs = ps.executeQuery();
      while (rs.next()) {
        games.add(transfer(rs));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return games;
  }

  public int getCurrentWeek() {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select * from game where date(start_time) <= current_date order by start_time desc");
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("week");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return 0;
  }
}
