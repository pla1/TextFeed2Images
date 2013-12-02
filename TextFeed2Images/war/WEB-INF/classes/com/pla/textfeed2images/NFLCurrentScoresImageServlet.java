package com.pla.textfeed2images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NFLCurrentScoresImageServlet extends HttpServlet {
  private static final long serialVersionUID = 5322608463347863514L;
  private String NFLbackgroundImagePath;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("image/png");
    GameDAO gameDAO = new GameDAO();
    ArrayList<Game> games = gameDAO.getGamesThisWeek();
    System.out.println("NFL Background: " + NFLbackgroundImagePath + " " + games.size() + " games. Date: " + new Date() + " "
        + this.getClass().getCanonicalName());
    InputStream nflBackgroundImageInputStream = getServletContext().getResourceAsStream(NFLbackgroundImagePath);
    NFLWeekImage nflWeekImage = new NFLWeekImage(games, nflBackgroundImageInputStream);
    BufferedImage bufferedImage = nflWeekImage.getBufferedImage();
    ImageIO.write(bufferedImage, "png", response.getOutputStream());
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    System.out.println(this.getClass().getCanonicalName() + " Start " + new Date());
    ServletConfig servletConfig = getServletConfig();
    NFLbackgroundImagePath = servletConfig.getServletContext().getInitParameter("NFL_background_image");
  }

}
