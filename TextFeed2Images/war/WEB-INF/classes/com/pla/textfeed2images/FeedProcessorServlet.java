package com.pla.textfeed2images;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
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

  private boolean isBlank(String s) {
    if (s == null || s.trim().length() == 0) {
      return true;
    }
    return false;
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
    if (isBlank(feedUrl)) {
      feedUrl = request.getParameter("feedUrl");
    }
    if (isBlank(feedUrl)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing feedUrl parameter");
      return;
    }
    URL url = new URL(feedUrl);
    ArrayList<Item> items = new ArrayList<Item>();
    String baseUrlString = getBaseUrlString(request);
    DrawText drawText = new DrawText();
    try {
      String description = "";
      String title = "";
      String pubdate = "";
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = url.openStream();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          String localPart = event.asStartElement().getName().getLocalPart();
          if (localPart.equals(TITLE)) {
            title = getCharacterData(event, eventReader);
          }
          if (localPart.equals(DESCRIPTION)) {
            description = getCharacterData(event, eventReader);
          }
          if (localPart.equals(PUB_DATE)) {
            pubdate = getCharacterData(event, eventReader);
          }
        } else {
          if (event.isEndElement()) {
            if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
              StringBuilder imageUrlString = new StringBuilder();
              if (true) {
                String path = getServletContext().getRealPath(".");
                if (!"localhost".equals(request.getServerName())) {
                  path = "/var/lib/tomcat6/webapps/TextFeed2Images";
                }
                int hashcode = (title + description + pubdate).hashCode();
                String fileName = hashcode + ".png";
                imageUrlString.append(baseUrlString);
                imageUrlString.append("images/");
                imageUrlString.append(fileName);
                File file = new File(path + "/images/" + fileName);
                if (file.exists()) {
                  System.out.println("File exists: " + file.getAbsolutePath());
                } else {
                  System.out.println(drawText.execute(file, title, description, pubdate));
                }
              } else {
                imageUrlString.append(getBaseUrlString(request));
                imageUrlString.append("/ImageServlet?title=");
                imageUrlString.append(URLEncoder.encode(title, "UTF-8"));
                imageUrlString.append("&description=");
                imageUrlString.append(URLEncoder.encode(description, "UTF-8"));
                imageUrlString.append("&dateString=");
                imageUrlString.append(URLEncoder.encode(pubdate, "UTF-8"));
              }
              Item item = new Item();
              item.setTitle(title);
              item.setDescription(description);
              item.setDateDisplay(pubdate);
              item.setLink(imageUrlString.toString());
              items.add(item);
              event = eventReader.nextEvent();
            }
          }
        }
      }
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
    try {
      writeNewFeed(request, response, items);
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }

  public void writeNewFeed(HttpServletRequest request, HttpServletResponse response, ArrayList<Item> items)
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
    createNode(eventWriter, "title", "Text Feed To Images");
    createNode(eventWriter, "link", "http://xbmc-rocks.com");
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

  private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
    String result = "";
    event = eventReader.nextEvent();
    if (event instanceof Characters) {
      result = event.asCharacters().getData();
    }
    return result;
  }

}
