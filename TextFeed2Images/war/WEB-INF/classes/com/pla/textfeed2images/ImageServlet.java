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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {
  private static final long serialVersionUID = 5322608463347863514L;
  private final int WIDTH = 1920;
  private final int HEIGHT = 1080;
  private final String TEMPORARY_DIRECTORY = System.getProperty("java.io.tmpdir");
  private static final String BLANK = "";
  private static final String SPACE = " ";

  private boolean isBlank(String s) {
    if (s == null || s.trim().length() == 0) {
      return true;
    }
    return false;
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String title = request.getParameter("title");
    String description = request.getParameter("description");
    String dateString = request.getParameter("dateString");
    if (isBlank(title)) {
      title = "Title parameter was not provided.";
    }
    if (isBlank(description)) {
      description = "Description parameter was not provided.";
    }
    if (isBlank(dateString)) {
      dateString = "Date string parameter was not provided.";
    }
    int hashcode = (title + description + dateString).hashCode();
    File file = new File(TEMPORARY_DIRECTORY + "/" + hashcode + ".png");
    boolean fileExists = file.exists();
    System.out.println("HASHCODE: " + hashcode + " TITLE: " + title + " DESCRIPTION: " + description + " DATE: " + dateString
        + " FILE: " + file.getAbsolutePath() + " EXISTS: " + fileExists);
    if (fileExists) {
      BufferedImage bi = ImageIO.read(file);
      OutputStream out = response.getOutputStream();
      ImageIO.write(bi, "png", out);
      out.close();
      return;
    }
    Rectangle rectangle = new Rectangle(0, 0, WIDTH, HEIGHT);
    BufferedImage bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setBackground(Color.WHITE);
    g2d.fill(rectangle);
    g2d.setColor(Color.BLACK);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font titleFont = new Font("Arial", Font.BOLD, 90);
    Font descriptionFont = new Font("Arial", Font.PLAIN, 90);
    Font dateFont = new Font("Arial", Font.PLAIN, 33);
    g2d.setFont(titleFont);
    AffineTransform affinetransform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    int textheight = (int) (titleFont.getStringBounds(title + title, frc).getHeight());
    String[] words = title.split(" ");
    StringBuilder sb = new StringBuilder();
    int textwidth = 0;
    int i = 0;
    int h = textheight;
    while (i < words.length) {
      while (textwidth < WIDTH && i < words.length) {
        String oneWordAhead = BLANK;
        if (i + 1 < words.length) {
          oneWordAhead = SPACE + words[i + 1];
        }
        sb.append(words[i++]).append(" ");
        textwidth = (int) (titleFont.getStringBounds(sb.toString() + oneWordAhead, frc).getWidth());
      }
      g2d.drawString(sb.toString(), 0, h);
      sb = new StringBuilder();
      textwidth = 0;
      h = h + textheight;
    }
    i = 0;
    words = description.split(SPACE);
    g2d.setFont(descriptionFont);
    while (i < words.length) {
      while (textwidth < WIDTH && i < words.length) {
        String oneWordAhead = BLANK;
        if (i + 1 < words.length) {
          oneWordAhead = SPACE + words[i + 1];
        }
        sb.append(words[i++]).append(" ");
        textwidth = (int) (descriptionFont.getStringBounds(sb.toString() + oneWordAhead, frc).getWidth());
      }
      g2d.drawString(sb.toString(), 0, h);
      sb = new StringBuilder();
      textwidth = 0;
      h = h + textheight;
    }
    g2d.setFont(dateFont);
    textwidth = (int) (dateFont.getStringBounds(dateString, frc).getWidth());
    textheight = (int) (dateFont.getStringBounds(dateString, frc).getHeight());
    g2d.drawString(dateString, WIDTH - textwidth, HEIGHT - textheight);
    response.setContentType("image/png");
    ImageIO.write(bufferedImage, "png", new FileOutputStream(file));
    ImageIO.write(bufferedImage, "png", response.getOutputStream());
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
