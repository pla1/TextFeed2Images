package com.pla.textfeed2images;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

public class NFLWeekImage {
  private ArrayList<Game> games;
  private InputStream nflBackgroundImageInputStream;

  public NFLWeekImage(ArrayList<Game> games, InputStream nflBackgroundImageInputStream) {
    this.games = games;
    this.nflBackgroundImageInputStream = nflBackgroundImageInputStream;
  }

  public BufferedImage getBufferedImage() {
    String FONT_NAME = "Arial";
    String title = Util.getNFLWeekTitle(games);
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(nflBackgroundImageInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font titleFont = new Font(FONT_NAME, Font.BOLD, 90);
    Font gameFont = new Font(FONT_NAME, Font.PLAIN, 60);
    Font dateFont = new Font(FONT_NAME, Font.PLAIN, 33);
    g2d.setFont(titleFont);
    AffineTransform affinetransform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    int textheight = (int) (titleFont.getStringBounds(title, frc).getHeight());
    int textwidth = (int) (titleFont.getStringBounds(title, frc).getWidth());
    int h = textheight;
    g2d.drawString(title, (Constants.WIDTH - textwidth) / 2, h);
    h = h + textheight;
    g2d.setFont(gameFont);
    textheight = (int) (gameFont.getStringBounds(title, frc).getHeight());
    int x = 0;
    for (Game game : games) {
      g2d.drawString(game.toString(), x, h);
      if (x == 0) {
        x = Constants.WIDTH / 2;
      } else {
        h = h + textheight + 20;
        x = 0;
      }
    }
    g2d.setFont(dateFont);
    String dateString = new Date().toString();
    textwidth = (int) (dateFont.getStringBounds(dateString, frc).getWidth());
    textheight = (int) (dateFont.getStringBounds(dateString, frc).getHeight());
    g2d.drawString(dateString, Constants.WIDTH - textwidth, Constants.HEIGHT - textheight);
    return bufferedImage;
  }
}
