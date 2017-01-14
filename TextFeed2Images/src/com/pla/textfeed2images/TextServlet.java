package com.pla.textfeed2images;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextServlet extends HttpServlet {

	private static final long serialVersionUID = -6844104107975869064L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("currentWeek".equals(action))
			currentWeek(request, response);
		else if ("currentYear".equals(action))
			currentYear(request, response);
		else
			write(response, String.format("Do not know what to do with action: %s", action));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private void currentWeek(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GameDAO gameDAO = new GameDAO();
		write(response, String.valueOf(gameDAO.getCurrentWeek()));
	}

	private void currentYear(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GameDAO gameDAO = new GameDAO();
		write(response, String.valueOf(gameDAO.getCurrentYear()));
	}

	private void write(HttpServletResponse response, String s) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.write(s);
		pw.flush();
		pw.close();
	}
}
