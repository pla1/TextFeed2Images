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

import javax.imageio.ImageIO;

public class DrawText {
  private final int WIDTH = 1920;
  private final int HEIGHT = 1080;
  private final String FONT_NAME = "Arial";

  public static void main(String[] args) throws Exception {
    DrawText drawText = new DrawText();
    File file = File.createTempFile("DrawText", ".png");
    String fileName = drawText
        .execute(
            file,
            "U.S. government rarely uses best cybersecurity steps: advisers",
            "NASA astronaut Tom Marshburn and Vickie Kloeris, the agency&#039;s manager of the International Space Station food system, will discuss the space station&#039;s Thanksgiving menus in live satellite interviews from 7- 8:30 a.m. EST Wednesday, Nov. 27.",
            "November 22, 2013", "ffbbdd", "aaeeee", false, 200);
    System.out.println(fileName);
    Runtime.getRuntime().exec("xdg-open " + fileName);
  }

  public DrawText() {
  }

  public Color adjustAlpha(Color color, int factor) {
    if (factor < 1 || factor > 255) {
      return color;
    }
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), factor);
  }

  private Color getColor(String hexValue) {
    if (hexValue == null) {
      return null;
    }
    try {
      return Color.decode("#" + hexValue);
    } catch (NumberFormatException nfe) {
      return null;
    }

  }

  public String execute(File file, String title, String description, String dateString, String foregroundColorHex,
      String backgroundColorHex, boolean invert, int transparency) {
    System.out.println("Image file:" + file.getAbsolutePath());
    Rectangle rectangle = new Rectangle(0, 0, WIDTH, HEIGHT);
    BufferedImage bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    Color foregroundColor = getColor(foregroundColorHex);
    Color backgroundColor = getColor(backgroundColorHex);
    if (backgroundColor == null) {
      if (invert) {
        backgroundColor = Color.BLACK;
      } else {
        backgroundColor = Color.WHITE;
      }
    }
    if (transparency != 0) {
      backgroundColor = adjustAlpha(backgroundColor, transparency);
    }
    g2d.setColor(backgroundColor);
    g2d.fill(rectangle);
    if (foregroundColor == null) {
      if (invert) {
        foregroundColor = Color.WHITE;
      } else {
        foregroundColor = Color.BLACK;
      }
    }
    g2d.setColor(foregroundColor);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font titleFont = new Font(FONT_NAME, Font.BOLD, 90);
    Font descriptionFont = new Font(FONT_NAME, Font.PLAIN, 90);
    Font dateFont = new Font(FONT_NAME, Font.PLAIN, 33);
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
        String oneWordAhead = "";
        if (i + 1 < words.length) {
          oneWordAhead = " " + words[i + 1];
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
    words = description.split(" ");
    g2d.setFont(descriptionFont);
    while (i < words.length) {
      while (textwidth < WIDTH && i < words.length) {
        String oneWordAhead = "";
        if (i + 1 < words.length) {
          oneWordAhead = " " + words[i + 1];
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
    try {
      ImageIO.write(bufferedImage, "png", file);
      return file.getAbsolutePath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
