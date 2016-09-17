package com.pla.textfeed2images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class GameDAO {
  private static final SimpleDateFormat hhmm = new SimpleDateFormat("h:mm");

  public static void main(String[] args) {
    GameDAO dao = new GameDAO();
    if (false) {
      int[] years = dao.getYears();
      for (int i : years) {
        System.out.println(i);
      }
      System.exit(0);
    }
    if (false) {
      System.out.println("Current year: " + dao.getCurrentYear());
      System.out.println("Current week: " + dao.getCurrentWeek());
      System.exit(0);
    }
    if (true) {
      ArrayList<Game> games = dao.getGamesThisWeek();
      for (Game game : games) {
        System.out.println(game + " " + game.getDate());
      }
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
    game.setFinished(rs.getBoolean("finished"));
    Timestamp timestamp = rs.getTimestamp("start_time");
    game.setStartTimeMilliseconds(Util.transfer(timestamp));
    game.setStartTimeDisplay(Util.getDateDisplay(timestamp));
    return game;
  }

  public ArrayList<Game> getGamesThisWeek() {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection
          .prepareStatement("select season_year, week, season_type "
              + "from game "
              + "where date(start_time) <= current_date "
              + "order by start_time desc");
      rs = ps.executeQuery();
      if (rs.next()) {
        int year = rs.getInt("season_year");
        int week = rs.getInt("week");
        String type = rs.getString("season_type");
        return getGames(year, week, type);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    Collections.sort(games);
    return games;
  }

  public int[] getYears() {
    ArrayList<Integer> arrayList = new ArrayList<Integer>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select season_year from game group by season_year order by season_year desc");
      rs = ps.executeQuery();
      while (rs.next()) {
        arrayList.add(rs.getInt("season_year"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    int[] years = new int[arrayList.size()];
    for (int i = 0; i < arrayList.size(); i++) {
      years[i] = arrayList.get(i).intValue();
    }
    return years;
  }

  public ArrayList<Game> getGames(int week) {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection
          .prepareStatement("select a.*, date(a.start_time) as gamedate,  "
              + "b.name as home_team_name, c.name as away_team_name, "
              + "start_time > current_timestamp as pending "
              + "from game as a "
              + "join team as b "
              + "on a.home_team = b.team_id "
              + "join team as c "
              + "on a.away_team = c.team_id "
              + "where week = ? "
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

  public ArrayList<Game> getGames(int year, int week, String type) {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      String sqlStatement = "select b.name as home_team_name, "
          + "c.name as away_team_name, "
          + "a.home_score, a.away_score, a.start_time, a.day_of_week, a.week, "
          + "a.season_year, a.season_type, a.finished, "
          + "date(a.start_time) as gamedate,  "
          + "b.name as home_team_name, c.name as away_team_name, "
          + "start_time > current_timestamp as pending "
          + "from game as a "
          + "join team as b "
          + "on a.home_team = b.team_id "
          + "join team as c "
          + "on a.away_team = c.team_id "
          + "where a.season_year = ? "
          + "and a.week = ? "
          + "and a.season_type::text = ? "
          + "order by start_time desc";
      System.out.println(sqlStatement);
      ps = connection.prepareStatement(sqlStatement);
      int i = 1;
      ps.setInt(i++, year);
      ps.setInt(i++, week);
      ps.setString(i++, type);
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

  public ArrayList<Game> getGames() {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection
          .prepareStatement("select a.*, date(a.start_time) as gamedate, b.name as home_team_name, c.name as away_team_name, "
              + "start_time > current_timestamp as pending from game as a "
              + "join team as b on a.home_team = b.team_id join team as c on a.away_team = c.team_id " + "order by start_time desc");
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

  public ArrayList<Game> getGames(int year, String team) {
    ArrayList<Game> games = new ArrayList<Game>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection
          .prepareStatement("select a.*, date(a.start_time) as gamedate, b.name as home_team_name, c.name as away_team_name, "
              + "start_time > current_timestamp as pending from game as a "
              + "join team as b on a.home_team = b.team_id join team as c on a.away_team = c.team_id where season_year = ? and ? in (home_team, away_team) and season_type <> 'Preseason' "
              + "order by start_time desc");
      ps.setInt(1, year);
      ps.setString(2, team);
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

  public int getCurrentYear() {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select * from game where date(start_time) <= current_date order by start_time desc");
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("season_year");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return 0;
  }
}
