package com.pla.textfeed2images;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

public class Util {
  private static Properties properties;
  public static void main(String[] args) throws Exception {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = ge.getAvailableFontFamilyNames();
    for (int i = 0; i < fontNames.length; i++) {
      System.out.println(fontNames[i]);
    }
  }

  static {
    String fileName = "/etc/com.pla.properties";
    System.out.println("Loading properties from file: " + fileName);
    try {
      InputStream input = new FileInputStream(new File(fileName));
      properties = new Properties();
      properties.load(input);
      System.out.println("Properties loaded from file: " + fileName);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Properties not loaded from file: " + fileName + " " + e.getLocalizedMessage());
    }
  }

  public static boolean isBlank(String s) {
    if (s == null || s.trim().length() == 0) {
      return true;
    }
    return false;
  }

  public static void runCommand(String s) {
    try {
      Runtime.getRuntime().exec(s);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean isNFLWeeInProgress(ArrayList<Game> games) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date fromDate = calendar.getTime();
    // TODO BUMP BACK DOWN TO 2 AFTER TESTING.
    calendar.add(Calendar.DAY_OF_YEAR, 2);
    Date toDate = calendar.getTime();
    boolean answer = false;
    for (Game game : games) {
      if (game.getDate().after(fromDate) && game.getDate().before(toDate)) {
        System.out.println(game.toString() + " " + game.getDate());
        answer = true;
      }
    }
    // System.out.println("Date range: " + fromDate + " " + toDate +
    // " In progress: " + answer);
    return answer;
  }

  public Util() {
  }

  public static int getNFLYear(ArrayList<Game> games) {
    if (games.isEmpty()) {
      return 0;
    }
    return games.get(0).getYear();
  }

  public static String getNFLWinLoss(Team team, ArrayList<Game> games) {
    if (games.isEmpty()) {
      return "";
    }
    int wins = 0;
    int losses = 0;
    int ties = 0;
    for (Game game : games) {
      if (game.isFinished()) {
        if (game.getAwayPoints() == game.getHomePoints()) {
          ties++;
        }
        if (team.getName().equals(game.getAwayTeam())) {
          if (game.getAwayPoints() > game.getHomePoints()) {
            wins++;
          }
          if (game.getAwayPoints() < game.getHomePoints()) {
            losses++;
          }
        } else {
          if (game.getAwayPoints() < game.getHomePoints()) {
            wins++;
          }
          if (game.getAwayPoints() > game.getHomePoints()) {
            losses++;
          }
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append(wins).append("-");
    sb.append(losses);
    if (ties > 0) {
      sb.append("-");
      sb.append(ties);
    }
    return sb.toString();
  }

  public static String getNFLWeekTitle(ArrayList<Game> games) {
    int week = 0;
    int year = 0;
    String seasonType = "";
    if (games.size() > 0) {
      Game game = games.get(0);
      week = game.getWeek();
      year = game.getYear();
      seasonType = game.getType();
    }
    StringBuilder sb = new StringBuilder();
    sb.append(year);
    sb.append(" ");
    if (!"Regular".equals(seasonType)) {
      sb.append(seasonType);
      sb.append(" ");
    }
    sb.append("NFL Week ");
    sb.append(week);
    return sb.toString();
  }

  public static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/nfldb", "nfldb", properties.getProperty("nfldb.password"));
  }

  public static void close(ResultSet rs, PreparedStatement ps, Connection connection) {
    close(rs);
    close(ps);
    close(connection);
  }

  public static void close(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void close(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void close(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean isDevelopmentEnvironment() {
    return Util.getIpAddress().startsWith("192.168.1.");
  }

  public static String getIpAddress() {
    String ipAddress = "";
    try {
      Socket socket = new Socket("google.com", 80);
      InetAddress inetAddress = socket.getLocalAddress();
      if (inetAddress != null) {
        ipAddress = inetAddress.getHostAddress();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ipAddress;
  }

}
