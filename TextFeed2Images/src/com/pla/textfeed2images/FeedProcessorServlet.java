package com.pla.textfeed2images;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FeedProcessorServlet extends HttpServlet {

  private static final long serialVersionUID = 41852420946556581L;
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String ITEM = "item";
  private static final String PUB_DATE = "pubDate";
  private static final String BLANK = "";
  private static final String LINK = "link";

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  private String getBaseUrlString(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    int port = request.getServerPort();
    if (port != 80) {
      sb.append(":");
      sb.append(port);
    }
    sb.append(request.getContextPath());
    sb.append("/");
    return sb.toString();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String feedUrl = request.getParameter("q");
    int transparency = getInt(request.getParameter("transparency"));
    String backgroundColor = request.getParameter("backgroundColor");
    String foregroundColor = request.getParameter("foregroundColor");
    boolean invert = isYes(request.getParameter("invert"));
    boolean destroy = isYes(request.getParameter("destroy"));
    if (transparency < 0 || transparency > 255) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter transparency must be between 0 and 255");
      return;
    }
    if (Util.isBlank(feedUrl)) {
      feedUrl = request.getParameter("feedUrl");
    }
    if (Util.isBlank(feedUrl)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing feedUrl parameter");
      return;
    }
    System.out.println("q: " + feedUrl + " transparency: " + transparency + " backgroundColor: " + backgroundColor
        + " foregroundColor: " + foregroundColor + " destroy: " + destroy);
    response.setContentType("application/rss+xml; charset=UTF-8");
    URL url = new URL(feedUrl);
    ArrayList<Item> items = new ArrayList<Item>();
    String baseUrlString = getBaseUrlString(request);
    String channelTitle = null;
    try {
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = url.openStream();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      items.addAll(getItems(eventReader, baseUrlString, destroy, foregroundColor, backgroundColor, invert, transparency));
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
    try {
      writeNewFeed(request, response, channelTitle, items);
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }

  private ArrayList<Item> getItems(XMLEventReader eventReader, String baseUrlString, boolean destroy, String foregroundColor,
      String backgroundColor, boolean invert, int transparency) throws XMLStreamException {
    ArrayList<Item> items = new ArrayList<Item>();
    StringBuilder sb = new StringBuilder();
    Item item = new Item();
    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();
      if (event.isEndElement()) {
        EndElement element = (EndElement) event;
        String elementName = element.getName().getLocalPart();
        System.out.println("End element:" + elementName);
        if (TITLE.equals(elementName)) {
          item.setTitle(clean(sb.toString()));
        }
        if (DESCRIPTION.equals(elementName)) {
          item.setDescription(clean(sb.toString()));
        }
        if (PUB_DATE.equals(elementName)) {
          item.setDateDisplay(sb.toString());
        }
        if (ITEM.equals(elementName)) {
          StringBuilder imageUrlString = new StringBuilder();
          if (true) {
            String path = getServletContext().getRealPath(".");
            if (Util.isDevelopmentEnvironment()) {
              path = "/home/htplainf/apache-tomcat-8.0.11/webapps/TextFeed2Images";
            } else {
              path = "/usr/share/apache-tomcat-8.0.30/webapps/TextFeed2Images";
            }
            int hashcode = item.toString().hashCode();
            String fileName = hashcode + ".png";
            imageUrlString.append(baseUrlString);
            imageUrlString.append("images/");
            imageUrlString.append(fileName);
            File file = new File(path + "/images/" + fileName);
            if (file.exists() && !destroy) {
              System.out.println("File exists: " + file.getAbsolutePath());
            } else {
              DrawText drawText = new DrawText();
              System.out.println(drawText.execute(file, item.getTitle(), item.getDescription(), item.getDateDisplay(),
                  foregroundColor, backgroundColor, invert, transparency));
            }
          }
          item.setLink(imageUrlString.toString());
          items.add(item);
          item = new Item();
        }
        sb = new StringBuilder();
      }
      if (event.isCharacters()) {
        Characters characters = (Characters) event;
        sb.append(characters.getData());
        sb.append(" ");
      }
    }
    return items;
  }

  private String getUrlString(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    if (request.getQueryString() != null) {
      requestURL.append("?").append(request.getQueryString());
    }
    return requestURL.toString();
  }

  public void writeNewFeed(HttpServletRequest request, HttpServletResponse response, String channelTitle, ArrayList<Item> items)
      throws ServletException, IOException, XMLStreamException {
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(response.getOutputStream());
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    StartDocument startDocument = eventFactory.createStartDocument();
    eventWriter.add(startDocument);
    eventWriter.add(end);
    StartElement rssStart = eventFactory.createStartElement(BLANK, BLANK, "rss");
    eventWriter.add(rssStart);
    eventWriter.add(eventFactory.createAttribute("version", "2.0"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createStartElement(BLANK, BLANK, "channel"));
    eventWriter.add(end);
    if (Util.isBlank(channelTitle)) {
      channelTitle = "RSS Feed To Images";
    }
    createNode(eventWriter, "title", channelTitle);
    createNode(eventWriter, "link", getUrlString(request));
    createNode(
        eventWriter,
        "description",
        "This web app reads a RSS feed, generates images of the text, and creates a new feed that is designed to be consumed by a XBMC home theater PC. Contact the author at Patrick.Archibald@gmail.com.");
    for (Item item : items) {
      eventWriter.add(eventFactory.createStartElement("", "", "item"));
      eventWriter.add(end);
      createNode(eventWriter, TITLE, item.getTitle());
      createNode(eventWriter, DESCRIPTION, item.getDescription());
      createNode(eventWriter, LINK, item.getLink());
      eventWriter.add(end);
      eventWriter.add(eventFactory.createEndElement(BLANK, BLANK, "item"));
      eventWriter.add(end);
    }
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement(BLANK, BLANK, "channel"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement(BLANK, BLANK, "rss"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.close();
  }

  private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    XMLEvent tab = eventFactory.createDTD("\t");
    StartElement sElement = eventFactory.createStartElement(BLANK, BLANK, name);
    eventWriter.add(tab);
    eventWriter.add(sElement);
    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);
    EndElement eElement = eventFactory.createEndElement(BLANK, BLANK, name);
    eventWriter.add(eElement);
    eventWriter.add(end);
  }

  private int getInt(String s) {
    int i = 0;
    try {
      i = Integer.parseInt(s);
    } catch (NumberFormatException nfe) {

    }
    return i;
  }

  private boolean isYes(String s) {
    if ("Y".equalsIgnoreCase(s)) {
      return true;
    }
    if ("Yes".equalsIgnoreCase(s)) {
      return true;
    }
    if ("true".equalsIgnoreCase(s)) {
      return true;
    }
    return false;
  }

  private String clean(String s) {
    s = s.replace("&apos;", "'");
    s = s.replaceAll("&gt;", ">");
    s = s.replaceAll("&lt;", "<");
    s = s.replaceAll("\\<[^>]*>", "").trim();
    return s.replaceAll("\\s+", " ");
  }
}
