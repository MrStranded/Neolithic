package engine.graphics.gui.statistics;

import engine.data.Data;
import engine.data.options.GameOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatisticsPanel extends JPanel {

    private int currentPosition = 0;
    private int width, height;
    private int border = 30;
    private BufferedImage img;

    public StatisticsPanel(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height-border, BufferedImage.TYPE_INT_RGB);
    }

    public void mark(double relativeHeight, Color color, int alpha) {
        int y = (int) ((height - border - 1) * (1 - relativeHeight));
        Graphics g = img.getGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y, 1, 1);
    }

    public void fatMark(double relativeHeight, Color color, int alpha) {
        int y = (int) ((height - border - 1) * (1 - relativeHeight));
        Graphics g = img.getGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y-1, 1, 3);
    }

    public void tick() {
        currentPosition = (currentPosition + 1) % width;

        Graphics g = img.getGraphics();
        g.setColor(new Color(255,255,255));
        g.drawRect(currentPosition, 0, 1, height - border);

        g.setColor(new Color(255, 150, 100));
        g.drawRect((currentPosition + 1) % width, 0 , 1, height - border);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0,0,width,height);
        g.drawImage(img, 0,border, null);

        engine.data.proto.Container container = Data.getContainer(GameOptions.currentContainerId);
        if (container != null) {
            g.setColor(new Color(200, 200, 200));
            g.drawRect(0,0,width,border);
            g.setColor(new Color(0,0,0));
            g.drawString("Current selection: " + container.getName(), 10, 20);
            g.drawString("Count: " + StatisticsData.getCount(GameOptions.currentContainerId), width/2 + 10, 20);
        }
    }

}
