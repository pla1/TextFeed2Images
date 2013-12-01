package com.pla.textfeed2images;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public class NFLCurrentScroreImageOLD {
  private final int WIDTH = 1920;
  private final int HEIGHT = 1080;
  private String title;

  public static void main(String[] args) {
    new NFLCurrentScroreImageOLD();
  }

  public NFLCurrentScroreImageOLD() {
    ArrayList<Game> games = getGames();
    String fileName = buildImage(games);
    // TODO remove below this
    System.out.println(fileName);
    try {
      Runtime.getRuntime().exec("xdg-open " + fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String upperCaseFirstLetter(String s) {
    if (s == null || s.trim().length() < 1) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  private String buildImage(ArrayList<Game> games) {
    Rectangle rectangle = new Rectangle(0, 0, WIDTH, HEIGHT);
    BufferedImage bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setBackground(Color.WHITE);
    g2d.fill(rectangle);
    g2d.setColor(Color.BLACK);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font titleFont = new Font("Arial", Font.BOLD, 90);
    Font gameFont = new Font("Arial", Font.PLAIN, 60);
    Font dateFont = new Font("Arial", Font.PLAIN, 33);
    g2d.setFont(titleFont);
    AffineTransform affinetransform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    int textheight = (int) (titleFont.getStringBounds(title, frc).getHeight());
    int h = textheight;
    g2d.drawString(title, 460, h);
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
    int textwidth = (int) (dateFont.getStringBounds(dateString, frc).getWidth());
    textheight = (int) (dateFont.getStringBounds(dateString, frc).getHeight());
    g2d.drawString(dateString, WIDTH - textwidth, HEIGHT - textheight);
    try {
      File file = new File("/tmp" + "/NFLCurrentScores.png");
      ImageIO.write(bufferedImage, "png", file);
      return file.getAbsolutePath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private ArrayList<Game> getGames() {
    ArrayList<Game> games = new ArrayList<Game>();
    try {
      URL url = new URL("http://www.nfl.com/liveupdate/scorestrip/ss.xml");
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = url.openStream();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        System.out.println("EVENT: " + event);
        if (event.isAttribute()) {
          System.out.println("ATTRIBUTE: " + event);
        }
        if (event.isStartElement()) {
          String localPart = event.asStartElement().getName().getLocalPart();
          System.out.println("START ELEMENT: " + localPart);
          Iterator<Attribute> attributes = event.asStartElement().getAttributes();
          if ("gms".equals(localPart)) {
            StringBuilder sb = new StringBuilder();
            int week = 0;
            int year = 0;
            while (attributes.hasNext()) {
              Attribute attribute = attributes.next();
              String name = attribute.getName().toString();
              String value = attribute.getValue();
              if ("y".equals(name)) {
                year = Integer.parseInt(value);
              }
              if ("w".equals(name)) {
                week = Integer.parseInt(value);
              }
            }
            sb.append("NFL Week ");
            sb.append(week).append(" ");
            sb.append(year);
            title = sb.toString();
          }
          if ("g".equals(localPart)) {
            Game game = new Game();
            games.add(game);
            while (attributes.hasNext()) {
              Attribute attribute = attributes.next();
              String name = attribute.getName().toString();
              String value = attribute.getValue();
              System.out.println(name + " = " + value);
              if ("d".equals(name)) {
                game.setDayOfWeek(attribute.getValue());
              }
              if ("eid".equals(name)) {
                game.setEventId(value);
              }
              if ("vnn".equals(name)) {
                game.setAwayTeam(upperCaseFirstLetter(value));
              }
              if ("hnn".equals(name)) {
                game.setHomeTeam(upperCaseFirstLetter(value));
              }
              if ("t".equals(name)) {
                game.setGameTime(value);
              }
              if ("q".equals(name)) {
                game.setQuarter(value);
              }
              if ("gt".equals(name)) {
                game.setType(value);
              }
              if ("hs".equals(name)) {
                game.setHomePoints(Integer.parseInt(value));
              }
              if ("vs".equals(name)) {
                game.setAwayPoints(Integer.parseInt(value));
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return games;
  }
}
