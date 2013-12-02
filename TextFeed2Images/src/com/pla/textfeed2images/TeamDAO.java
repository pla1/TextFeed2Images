package com.pla.textfeed2images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TeamDAO {

  public static void main(String[] args) {
    TeamDAO teamDAO = new TeamDAO();
    if (false) {
      System.out.println(teamDAO.get("CAR"));
    }
    if (true) {
      ArrayList<Team> teams = teamDAO.get();
      for (Team team : teams) {
        System.out.println(team);
      }
    }
  }

  private Team transfer(ResultSet rs) throws SQLException {
    Team team = new Team();
    team.setCity(rs.getString("city"));
    team.setName(rs.getString("name"));
    team.setTeamId(rs.getString("team_id"));
    team.setFound(true);
    return team;
  }

  public Team get(String teamId) {
    Team team = new Team();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select * from team where team_id = ?");
      ps.setString(1, teamId);
      rs = ps.executeQuery();
      if (rs.next()) {
        team = transfer(rs);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return team;
  }

  public ArrayList<Team> get() {
    ArrayList<Team> teams = new ArrayList<Team>();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = Util.getConnection();
      ps = connection.prepareStatement("select * from team where team_id <> 'UNK' order by team_id");
      rs = ps.executeQuery();
      while (rs.next()) {
        teams.add(transfer(rs));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Util.close(rs, ps, connection);
    }
    return teams;
  }
}
