package com.pla.textfeed2images;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StartupServlet extends HttpServlet {

  class StartupThread extends Thread {
    public StartupThread() {
      setDaemon(true);
      System.out.println("StartupThread started");
    }

    public void run() {
      System.out.println(this.getClass().getCanonicalName() + " Start. " + new Date());
      System.out.println("**** START FONTS *****");
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("/usr/share/fonts/truetype/msttcorefonts/Arial.ttf")));
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("/usr/share/fonts/truetype/msttcorefonts/Arial_Bold.ttf")));
      } catch (Exception e2) {
        e2.printStackTrace();
      }
      String[] fontNames = ge.getAvailableFontFamilyNames();
      for (int i = 0; i < fontNames.length; i++) {
        System.out.println(fontNames[i]);
      }
      System.out.println("**** END FONTS *****");
      NFLbackgroundImagePath = servletConfig.getServletContext().getInitParameter("NFL_background_image");
      System.out.println(NFLbackgroundImagePath);
      System.out.println("Real path:" + servletConfig.getServletContext().getRealPath("."));
      Set<String> set = servletConfig.getServletContext().getResourcePaths("/");
      System.out.println("Set size: " + set.size());
      for (String s : set) {
        System.out.println("PATH: " + s);
      }
      try {
        System.out.println("URL: " + servletConfig.getServletContext().getResource(NFLbackgroundImagePath));
      } catch (MalformedURLException e1) {
        e1.printStackTrace();
      }
      GameDAO gameDAO = new GameDAO();
      ArrayList<Game> games = gameDAO.getGames();
      System.out.println(this.getClass().getCanonicalName() + " Writing NFL score images.  " + games.size() + " games. "
          + new Date());
      try {
        writeNewFeed(games);
        System.out.println(this.getClass().getCanonicalName() + " Done. " + new Date());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static final long serialVersionUID = -7249315698504496077L;
  private String NFLbackgroundImagePath;
  private ServletConfig servletConfig;

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

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    this.servletConfig = servletConfig;
    StartupThread startupThread = new StartupThread();
    startupThread.start();
  }

  private void writeNewFeed(ArrayList<Game> games) throws ServletException, IOException, XMLStreamException {
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    String urlBase;
    if (Util.isDevelopmentEnvironment()) {
      urlBase = "http://192.168.1.14:8080/TextFeed2Images/";
    } else {
      urlBase = "http://xbmc-rocks.com/";
    }
    String path = getServletConfig().getServletContext().getRealPath("/");
    System.out.println("REAL PATH: " + getServletContext().getRealPath(NFLbackgroundImagePath));
    File file = new File(path + "nfl.rss");
    System.out.println(file.getAbsolutePath());
    FileOutputStream outputStream = new FileOutputStream(file);
    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputStream);
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
    createNode(eventWriter, "title", "NFL Scores");
    createNode(eventWriter, "link", "http://xbmc-rocks.com/nfl.rss");
    createNode(eventWriter, "image", "http://xbmc-rocks.com/NFL1920x1080.png");
    createNode(
        eventWriter,
        "description",
        "This web app reads a database table, generates images of NFL scores, and creates a new feed that is designed to be consumed by a XBMC home theater PC. Contact the author at Patrick.Archibald@gmail.com.");
    eventWriter.add(end);
    eventWriter.add(eventFactory.createStartElement(Constants.BLANK, Constants.BLANK, "item"));
    createNode(eventWriter, Constants.TITLE, "NFL latest scores");
    createNode(eventWriter, Constants.DESCRIPTION, "NFL latest scrores");
    createNode(eventWriter, Constants.LINK, urlBase + "NFLCurrentScoresImageServlet.png");
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, "item"));
    eventWriter.add(end);
    int year = games.get(0).getYear();
    int week = games.get(0).getWeek();
    ArrayList<Game> weekOfGames = new ArrayList<Game>();
    for (Game game : games) {
      if (year == game.getYear() && week == game.getWeek()) {
        weekOfGames.add(game);
      } else {
        InputStream nflBackgroundImageInputStream = getServletContext().getResourceAsStream(NFLbackgroundImagePath);
        NFLWeekImage nflWeekImage = new NFLWeekImage(weekOfGames, nflBackgroundImageInputStream);
        BufferedImage bufferedImage = nflWeekImage.getBufferedImage();
        if (!path.endsWith("/")) {
          path = path + "/";
        }
        String imageFileName = path + "images/" + weekOfGames.get(0).getImageFileName();
        File imageFile = new File(imageFileName);
        System.out.println("About to write: " + imageFile.getAbsolutePath());
        ImageIO.write(bufferedImage, "png", imageFile);
        eventWriter.add(eventFactory.createStartElement(Constants.BLANK, Constants.BLANK, "item"));
        eventWriter.add(end);
        String title = Util.getNFLWeekTitle(weekOfGames);
        createNode(eventWriter, Constants.TITLE, title);
        createNode(eventWriter, Constants.DESCRIPTION, title);
        if (Util.isNFLWeeInProgress(weekOfGames)) {
          createNode(eventWriter, Constants.LINK, urlBase + "NFLCurrentScoresImageServlet.png");
        } else {
          createNode(eventWriter, Constants.LINK, urlBase + "images/" + weekOfGames.get(0).getImageFileName());
        }
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement(Constants.BLANK, Constants.BLANK, "item"));
        eventWriter.add(end);
        weekOfGames = new ArrayList<Game>();
        weekOfGames.add(game);
        year = game.getYear();
        week = game.getWeek();
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

}
