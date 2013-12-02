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

public class NFLCurrentStandingsImageServlet extends HttpServlet {
  private static final long serialVersionUID = 5322608463347863514L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("image/png");
    TeamDAO teamDAO = new TeamDAO();
    Team team = teamDAO.get(request.getParameter("q"));
    GameDAO gameDAO = new GameDAO();
    int yyyy = gameDAO.getCurrentYear();
    ArrayList<Game> games = gameDAO.getGames(yyyy, team.getTeamId());
    String fileName = "/WEB-INF/resources/" + team.getTeamId() + ".jpg";
    System.out.println("NFL team background: " + fileName + " " + games.size() + " games. Date: " + new Date() + " "
        + this.getClass().getCanonicalName());
    InputStream nflBackgroundImageInputStream = getServletContext().getResourceAsStream(fileName);
    NFLTeamImage nflTeamImage = new NFLTeamImage(team.getTeamId(), games, nflBackgroundImageInputStream);
    BufferedImage bufferedImage = nflTeamImage.getBufferedImage();
    ImageIO.write(bufferedImage, "png", response.getOutputStream());
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    System.out.println(this.getClass().getCanonicalName() + " Start " + new Date());
    ServletConfig servletConfig = getServletConfig();
  }

}
