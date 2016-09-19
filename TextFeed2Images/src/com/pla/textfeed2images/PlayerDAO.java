package com.pla.textfeed2images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerDAO {

  public static void main(String[] args) {
    PlayerDAO dao = new PlayerDAO();
    if (false) {
      ArrayList<Player> players = dao.getPassing();
      for (Player p : players) {
        System.out.format("%s %s %d\n", p.getFullName(), p.getTeam(), p.getAggregate());
      }
    }
    if (false) {
      ArrayList<Player> players = dao.getReceiving();
      for (Player p : players) {
        System.out.format("%s %s %d\n", p.getFullName(), p.getTeam(), p.getAggregate());
      }
    }
    if (true) {
      ArrayList<Player> players = dao.getRushing();
      for (Player p : players) {
        System.out.format("%s %s %d\n", p.getFullName(), p.getTeam(), p.getAggregate());
      }
    }
  }

  private Player transfer(ResultSet rs) throws SQLException {
    Player p = new Player();
    return p;
  }

  public ArrayList<Player> getReceiving() {
    String sqlStatement = "SELECT player.full_name, team.name as team_name, " +
        "SUM(play_player.receiving_yds) AS aggregate " +
        "FROM play_player " +
        "JOIN player ON player.player_id = play_player.player_id " +
        "JOIN game ON game.gsis_id = play_player.gsis_id " +
        "join team on player.team = team.team_id " +
        "join meta on game.season_year = meta.season_year " +
        "AND game.season_type = meta.season_type " +
        "GROUP BY player.full_name, team_name " +
        "ORDER BY aggregate DESC";
    return getAggregate(sqlStatement);
  }

  public ArrayList<Player> getPassing() {
    String sqlStatement = "SELECT player.full_name, team.name as team_name, " +
        "SUM(play_player.passing_yds) AS aggregate " +
        "FROM play_player " +
        "JOIN player ON player.player_id = play_player.player_id " +
        "JOIN game ON game.gsis_id = play_player.gsis_id " +
        "join team on player.team = team.team_id " +
        "join meta on game.season_year = meta.season_year " +
        "AND game.season_type = meta.season_type " +
        "GROUP BY player.full_name, team_name " +
        "ORDER BY aggregate DESC";
    return getAggregate(sqlStatement);
  }
  public ArrayList<Player> getRushing() {
    String sqlStatement = "SELECT player.full_name, team.name as team_name, " +
        "SUM(play_player.rushing_yds) AS aggregate " +
        "FROM play_player " +
        "JOIN player ON player.player_id = play_player.player_id " +
        "JOIN game ON game.gsis_id = play_player.gsis_id " +
        "join team on player.team = team.team_id " +
        "join meta on game.season_year = meta.season_year " +
        "AND game.season_type = meta.season_type " +
        "GROUP BY player.full_name, team_name " +
        "ORDER BY aggregate DESC";
    return getAggregate(sqlStatement);
  }


  public ArrayList<Player> getAggregate(String sqlStatement) {
    ArrayList<Player> players = new ArrayList<Player>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement(sqlStatement);
      rs = ps.executeQuery();
      while (rs.next()) {
        Player player = new Player();
        player.setFullName(rs.getString("full_name"));
        if (player.getFullName() != null && player.getFullName().contains(".")) {
          player.setFullName(player.getFullName().replaceAll("\\.", " "));
        }
        player.setTeam(rs.getString("team_name"));
        player.setAggregate(rs.getInt("aggregate"));
        players.add(player);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return players;
  }

  public ArrayList<Player> get() {
    ArrayList<Player> players = new ArrayList<Player>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select * from player");
      rs = ps.executeQuery();
      while (rs.next()) {
        players.add(transfer(rs));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return players;
  }
}
