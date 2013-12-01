package com.pla.textfeed2images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Util {
  public static final String BLANK = "";
  public static final String TITLE = "title";
  public static final String DESCRIPTION = "description";
  public static final String ITEM = "item";
  public static final String PUB_DATE = "pubDate";
  public static final String LINK = "link";

  public static void main(String[] args) throws Exception {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = ge.getAvailableFontFamilyNames();
    for (int i = 0; i < fontNames.length; i++) {
      System.out.println(fontNames[i]);
    }
  }

  public static boolean isBlank(String s) {
    if (s == null || s.trim().length() == 0) {
      return true;
    }
    return false;
  }

  public static boolean isNFLWeeInProgress(ArrayList<Game> games) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date fromDate = calendar.getTime();
    // TODO BUMP BACK DOWN TO 2 AFTER TESTING.
    calendar.add(Calendar.DAY_OF_YEAR, 5);
    Date toDate = calendar.getTime();
    boolean answer = false;
    for (Game game : games) {
      if (game.getDate().after(fromDate) && game.getDate().before(toDate)) {
        System.out.println(game.toString() + " " + game.getDate());
        answer = true;
      }
    }
    System.out.println("Date range: " + fromDate + " " + toDate + " In progress: " + answer);
    return answer;
  }

  public Util() {
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

  public static BufferedImage toBufferedImage(ArrayList<Game> games, InputStream nflBackgroundImageInputStream) {
    int WIDTH = 1920;
    int HEIGHT = 1080;
    String FONT_NAME = "Arial";
    String title = Util.getNFLWeekTitle(games);
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(nflBackgroundImageInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font titleFont = new Font(FONT_NAME, Font.BOLD, 90);
    Font gameFont = new Font(FONT_NAME, Font.PLAIN, 60);
    Font dateFont = new Font(FONT_NAME, Font.PLAIN, 33);
    g2d.setFont(titleFont);
    AffineTransform affinetransform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    int textheight = (int) (titleFont.getStringBounds(title, frc).getHeight());
    int textwidth = (int) (titleFont.getStringBounds(title, frc).getWidth());
    int h = textheight;
    g2d.drawString(title, (WIDTH - textwidth) / 2, h);
    h = h + textheight;
    g2d.setFont(gameFont);
    textheight = (int) (gameFont.getStringBounds(title, frc).getHeight());
    int x = 0;
    for (Game game : games) {
      g2d.drawString(game.toString(), x, h);
      if (x == 0) {
        x = WIDTH / 2;
      } else {
        h = h + textheight + 20;
        x = 0;
      }
    }
    g2d.setFont(dateFont);
    String dateString = new Date().toString();
    textwidth = (int) (dateFont.getStringBounds(dateString, frc).getWidth());
    textheight = (int) (dateFont.getStringBounds(dateString, frc).getHeight());
    g2d.drawString(dateString, WIDTH - textwidth, HEIGHT - textheight);
    return bufferedImage;
  }

  public static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/nfldb", "nfldb", "nfldb");
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
