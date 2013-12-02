package com.pla.textfeed2images;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class NFLTeamImage {
  private ArrayList<Game> games;
  private InputStream nflBackgroundImageInputStream;
  private String teamId;
  private Font titleFont = new Font("Arial", Font.BOLD, 90);
  private Font gameFont = new Font("Arial", Font.PLAIN, 50);
  private Font dateFont = new Font("Arial", Font.PLAIN, 33);
  private AffineTransform affinetransform = new AffineTransform();
  private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd");

  public static void main(String[] args) throws Exception {
    NFLTeamImage nflTeamImage = new NFLTeamImage();
    if (Util.isDevelopmentEnvironment()) {
      GameDAO gameDAO = new GameDAO();
      ArrayList<Game> games = gameDAO.getGames(2013, "CAR");
      File backgroundFile = new File("/home/htplainf/apache-tomcat-7.0.42/webapps/TextFeed2Images/WEB-INF/resources/CAR.jpg");
      FileInputStream nflBackgroundImageInputStream = new FileInputStream(backgroundFile);
      NFLTeamImage image = new NFLTeamImage("CAR", games, nflBackgroundImageInputStream);
      File outputFile = new File("/tmp/CAR.png");
      ImageIO.write(image.getBufferedImage(), "png", outputFile);
      Util.runCommand("google-chrome " + outputFile.getAbsolutePath());
      System.exit(0);
    }
    if (args.length > 0 && "createImagesAndFeeds".equals(args[0])) {
      nflTeamImage.createImagesAndFeeds();
    }
  }

  public NFLTeamImage() {
  }

  private void createImagesAndFeeds() throws Exception {
    GameDAO gameDAO = new GameDAO();
    TeamDAO teamDAO = new TeamDAO();
    ArrayList<Team> teams = teamDAO.get();
    int[] years = gameDAO.getYears();
    createFeeds(years, teams);
    createImages(years, teams);
  }

  private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    XMLEvent tab = eventFactory.createDTD("\t");
    StartElement sElement = eventFactory.createStartElement(Constants.BLANK, Constants.BLANK, name);
    eventWriter.add(tab);
    eventWriter.add(sElement);
    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);
    EndElement eElement = eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, name);
    eventWriter.add(eElement);
    eventWriter.add(end);
  }

  private void createFeeds(int[] years, ArrayList<Team> teams) throws Exception {
    File outputFile;
    if (Util.isDevelopmentEnvironment()) {
      outputFile = new File("/home/htplainf/apache-tomcat-7.0.42/webapps/TextFeed2Images/NFLstandings.rss");
    } else {
      outputFile = new File("/var/lib/tomcat7/webapps/TextFeed2Images/NFLstandings.rss");
    }
    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(fileOutputStream);
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    StartDocument startDocument = eventFactory.createStartDocument();
    eventWriter.add(startDocument);
    eventWriter.add(end);
    StartElement rssStart = eventFactory.createStartElement(Constants.BLANK, Constants.BLANK, "rss");
    eventWriter.add(rssStart);
    eventWriter.add(eventFactory.createAttribute("version", "2.0"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createStartElement(Constants.BLANK, Constants.BLANK, "channel"));
    eventWriter.add(end);
    createNode(eventWriter, "title", "NFL Team Standings");
    createNode(eventWriter, "link", "http://xbmc-rocks.com/NFLstandings.rss");
    createNode(
        eventWriter,
        Constants.DESCRIPTION,
        "This web app generates images of NFL team standings and creates a RSS feed that is designed to be consumed by a XBMC home theater PC. Contact the author at Patrick.Archibald@gmail.com.");
    for (Team team : teams) {
      for (int year : years) {
        StringBuilder sb = new StringBuilder();
        sb.append(team.getCity()).append(" ");
        sb.append(team.getName());
        String teamFullName = sb.toString();
        eventWriter.add(eventFactory.createStartElement("", "", "item"));
        eventWriter.add(end);
        createNode(eventWriter, Constants.TITLE, year + " " + teamFullName);
        createNode(eventWriter, Constants.DESCRIPTION, year + " " + teamFullName);
        String urlString;
        if (Util.isDevelopmentEnvironment()) {
          urlString = "http://localhost:8080/TextFeed2Images/images/" + year + team.getTeamId() + ".png";
        } else {
          urlString = "http://xbmc-rocks.com/images/" + year + team.getTeamId() + ".png";
        }
        createNode(eventWriter, Constants.LINK, urlString);
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, "item"));
        eventWriter.add(end);
      }
    }
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, "channel"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, "rss"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.close();
  }

  private void createImages(int[] years, ArrayList<Team> teams) throws Exception {
    GameDAO gameDAO = new GameDAO();
    for (int year : years) {
      for (Team team : teams) {
        ArrayList<Game> games = gameDAO.getGames(year, team.getTeamId());
        File outputFile;
        File backgroundFile;
        if (Util.isDevelopmentEnvironment()) {
          outputFile = new File("/home/htplainf/apache-tomcat-7.0.42/webapps/TextFeed2Images/images/" + year + team.getTeamId()
              + ".png");
          backgroundFile = new File("/home/htplainf/apache-tomcat-7.0.42/webapps/TextFeed2Images/WEB-INF/resources/"
              + team.getTeamId() + ".jpg");
        } else {
          outputFile = new File("/var/lib/tomcat7/webapps/TextFeed2Images/images/" + year + team.getTeamId() + ".png");
          backgroundFile = new File("/var/lib/tomcat7/webapps/TextFeed2Images/WEB-INF/resources/" + team.getTeamId() + ".jpg");
        }
        InputStream is = new FileInputStream(backgroundFile);
        NFLTeamImage nflTeamImage = new NFLTeamImage(team.getTeamId(), games, is);
        ImageIO.write(nflTeamImage.getBufferedImage(), "png", outputFile);
        System.out.println(outputFile.getAbsolutePath());
      }
    }

  }

  public NFLTeamImage(String teamId, ArrayList<Game> games, InputStream nflBackgroundImageInputStream) {
    this.games = games;
    this.nflBackgroundImageInputStream = nflBackgroundImageInputStream;
    this.teamId = teamId;
  }

  public BufferedImage getBufferedImage() {
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(nflBackgroundImageInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setFont(titleFont);
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    TeamDAO teamDAO = new TeamDAO();
    Team team = teamDAO.get(teamId);
    StringBuilder sb = new StringBuilder();
    sb.append(Util.getNFLYear(games)).append(" ");
    sb.append(team.getCity()).append(" ");
    sb.append(team.getName()).append(" ");
    sb.append(Util.getNFLWinLoss(team, games));
    String title = sb.toString();
    int textheight = (int) (titleFont.getStringBounds(title, frc).getHeight());
    int textwidth = (int) (titleFont.getStringBounds(title, frc).getWidth());
    int h = textheight;
    g2d.drawString(title, (Constants.WIDTH - textwidth) / 2, h);
    h = h + textheight;
    int firstLineHeight = h;
    g2d.setFont(gameFont);
    textheight = (int) (gameFont.getStringBounds(title, frc).getHeight());
    int offset = 0;
    int quantity = 0;
    for (Game game : games) {
      if (quantity++ > 7 && offset == 0) {
        offset = Constants.WIDTH / 2;
        h = firstLineHeight;
      }
      g2d.drawString(sdf.format(game.getDate()), 5 + offset, h);
      if (team.getName().equals(game.getHomeTeam())) {
        g2d.drawString("vs", 300 + offset, h);
        g2d.drawString(game.getAwayTeam(), 370 + offset, h);
        if (game.isFinished()) {
          if (game.getHomePoints() > game.getAwayPoints()) {
            g2d.drawString("W", 680 + offset, h);
          }
          if (game.getHomePoints() < game.getAwayPoints()) {
            g2d.setColor(Color.RED);
            g2d.drawString("L", 680 + offset, h);
            g2d.setColor(Color.BLACK);
          }
          if (game.getHomePoints() == game.getAwayPoints()) {
            g2d.drawString("T", 680 + offset, h);
          }
          g2d.drawString(game.getHomePoints() + "-" + game.getAwayPoints(), 750 + offset, h);
        }
      } else {
        g2d.drawString("@", 300 + offset, h);
        g2d.drawString(game.getHomeTeam(), 370 + offset, h);
        if (game.isFinished()) {
          if (game.getHomePoints() < game.getAwayPoints()) {
            g2d.drawString("W", 680 + offset, h);
          }
          if (game.getHomePoints() > game.getAwayPoints()) {
            g2d.setColor(Color.RED);
            g2d.drawString("L", 680 + offset, h);
            g2d.setColor(Color.BLACK);
          }
          if (game.getHomePoints() == game.getAwayPoints()) {
            g2d.drawString("T", 680 + offset, h);
          }
          g2d.drawString(game.getAwayPoints() + "-" + game.getHomePoints(), 750 + offset, h);
        }
      }
      if (!game.isFinished()) {
        g2d.drawString(game.getGameTime() + " " + game.getDayOfWeekDisplay(), 680 + offset, h);
      }
      h = h + textheight + 20;
    }
    g2d.setFont(dateFont);
    String dateString = new Date().toString();
    textwidth = (int) (dateFont.getStringBounds(dateString, frc).getWidth());
    textheight = (int) (dateFont.getStringBounds(dateString, frc).getHeight());
    g2d.drawString(dateString, Constants.WIDTH - textwidth, Constants.HEIGHT - textheight);
    return bufferedImage;
  }

}
