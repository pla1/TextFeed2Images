package com.pla.textfeed2images;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonServlet extends HttpServlet {

  private static final long serialVersionUID = -6844104107975869064L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (Util.isDevelopmentEnvironment()) {
      response.setHeader("Access-Control-Allow-Origin", "*");
    }
    String action = request.getParameter("action");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    if ("games".equals(action))
      games(request, response, gson);
    else if ("teams".equals(action))
      teams(request, response, gson);
    else if ("years".equals(action))
      years(request, response, gson);
    else if ("weeks".equals(action))
      weeks(request, response, gson);
    else if ("flashBriefing".equals(action))
      flashBriefing(request, response, gson);
    else
      write(response, String.format("Do not know what to do with action: %s", action));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  private void teams(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
    TeamDAO dao = new TeamDAO();
    write(response, gson.toJson(dao.get()));
  }

  private void games(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
    GameDAO dao = new GameDAO();
    write(response, gson.toJson(dao.getGames()));
  }

  private void flashBriefing(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
    FlashBriefingDAO dao = new FlashBriefingDAO();
    String type = request.getParameter("type");
    if (Util.isBlank(type) || "nflCurrentWeek".equals(type)) {
      write(response, gson.toJson(dao.getNflCurrentWeek()));
    }
  }

  private void weeks(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
    WeekDAO dao = new WeekDAO();
    write(response, gson.toJson(dao.get()));
  }

  private void years(HttpServletRequest request, HttpServletResponse response, Gson gson) throws IOException {
    GameDAO dao = new GameDAO();
    write(response, gson.toJson(dao.getYears()));
  }

  private void write(HttpServletResponse response, String s) throws IOException {
    PrintWriter pw = response.getWriter();
    pw.write(s);
    pw.flush();
    pw.close();
  }
}
